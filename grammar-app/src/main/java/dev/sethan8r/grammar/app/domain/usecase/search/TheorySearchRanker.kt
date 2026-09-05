package dev.sethan8r.grammar.app.domain.usecase.search

import dev.sethan8r.grammar.app.domain.model.theory.IndexedMicrotopic
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicState
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicSummary
import dev.sethan8r.grammar.app.domain.model.theory.SearchGroup
import dev.sethan8r.grammar.app.domain.model.theory.SearchIndex
import dev.sethan8r.grammar.app.domain.model.theory.TopicSummary
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Отбирает и ранжирует выдачу поиска. Чистый Kotlin — тестируется без Android; про прогресс
 * не знает, статусы приходят готовыми в [SearchIndex].
 *
 * Модель та же, что в полнотекстовых движках, урезанная до нашего масштаба (полторы сотни
 * микротем, полный пересчёт на каждый символ). Очки строки индекса складываются из трёх
 * множителей:
 * - **вес поля** — попадание в название дороже попадания в тег, тег дороже названия карточки;
 * - **сколько запроса покрыто** — с поправкой на редкость слов: совпасть по `подлежащее` весомее,
 *   чем по `нужно`, которое встречается по всему курсу;
 * - **сколько поля покрыто** — короткий тег, совпавший целиком, точнее длинного названия,
 *   зацепившегося одним словом.
 *
 * Отсюда же следует правило «ответ на запрос про тему — сама тема»: микротемы разворачиваются
 * только если бьют собственную тему по очкам.
 *
 * Веса — в [Weights]; разбор решений — search_feature_brief.md §11.
 */
class TheorySearchRanker @Inject constructor(
    private val normalizer: SearchNormalizer,
) {

    fun prepare(index: SearchIndex): PreparedIndex = PreparedIndex.of(index, normalizer)

    fun rank(index: PreparedIndex, query: String): List<SearchGroup> {
        byCardId(index, query)?.let { return it }

        val terms = parse(index, query)
        if (terms.isEmpty() || terms.all { it.isFunctionWord }) return emptyList()

        val parsed = Query(terms, reach(index, terms))
        val found = index.topics.mapNotNull { topic -> group(topic, parsed) }
        // Сначала отсекаем по тому, СКОЛЬКО запроса тема объясняет, и только потом по очкам:
        // на «прошедшее время» тема про прошедшее должна вытеснить всё, что зацепилось одним
        // словом «время». Покрытие считается по весам слов, поэтому редкое слово перевешивает
        // пару общих.
        val bestCovered = found.maxOfOrNull { it.covered } ?: return emptyList()
        val groups = found
            .filter { it.covered >= bestCovered * COVERAGE_TIER_RATIO }
            .map { it.group }
            .sortedWith(compareByDescending<SearchGroup> { it.score }.thenBy { it.topic.id })
        val best = groups.first().score
        val floor = maxOf(best * GROUP_NOISE_RATIO, MIN_GROUP_SCORE.toDouble())
        return groups.filter { it.score >= floor }
    }

    /**
     * Запрос из одних цифр — это номер карточки с бейджа читалки: человек смотрит на скриншот и
     * не хочет набирать название микротемы. Отдаём микротему, которой карточка принадлежит, обычной
     * группой выдачи. Возвращает null, если запрос не числовой (тогда работает обычный поиск), и
     * пустую выдачу, если карточки с таким номером нет.
     */
    private fun byCardId(index: PreparedIndex, query: String): List<SearchGroup>? {
        val digits = query.trim()
        if (digits.isEmpty() || !digits.all(Char::isDigit)) return null
        val cardId = digits.toIntOrNull() ?: return emptyList()

        for (topic in index.topics) {
            val microtopic = topic.microtopics.firstOrNull { cardId in it.source.cardIds } ?: continue
            return listOf(
                SearchGroup(
                    topic = topic.toSummary(),
                    sectionTitle = topic.source.sectionTitle,
                    microtopics = listOf(microtopic.source.toSummary()),
                    // Попадание по номеру точное, соперников у него нет — очки взяты по верхней
                    // планке весов только чтобы поле не осталось бессмысленным нулём.
                    score = Weights.MICROTOPIC_TITLE.roundToInt(),
                ),
            )
        }
        return emptyList()
    }

    /**
     * Слова запроса с весами. Слово, которого в курсе нет вовсе, выбрасывается: найти оно ничего
     * не может, а вес свой в знаменатель покрытия внесло бы — именно так терялись длинные
     * вопросы вроде «почему подлежащее нужно всегда».
     */
    private fun parse(index: PreparedIndex, query: String): List<QueryTerm> {
        val stems = normalizer.stems(query)
        return stems.mapIndexedNotNull { position, stem ->
            // Префикс разрешён только последнему слову — пользователь его ещё печатает.
            val match = index.resolve(stem, allowPrefix = position == stems.lastIndex, normalizer)
            if (match.stems.isEmpty()) {
                null
            } else {
                val isFunctionWord = normalizer.isFunctionWord(stem)
                val weight = if (isFunctionWord) {
                    FUNCTION_WORD_WEIGHT
                } else {
                    index.weight(match.documentFrequency).coerceAtLeast(MIN_TERM_WEIGHT)
                }
                QueryTerm(match.stems, weight, isFunctionWord)
            }
        }
    }

    /**
     * Сколько запроса вообще способна объяснить лучшая тема курса. Покрытие считается в долях от
     * этого, а не от всего запроса: иначе лишние слова вокруг ключевого («почему … нужно всегда»)
     * занижали бы очки настоящего ответа просто за то, что человек спросил предложением.
     */
    private fun reach(index: PreparedIndex, terms: List<QueryTerm>): Double = index.topics.maxOf { topic ->
        val stems = topic.stems + topic.microtopics.flatMap { it.stems }
        terms.filter { term -> stems.any { term.quality(it) != null } }.sumOf { it.weight }
    }

    private class QueryTerm(
        private val matches: Map<String, MatchQuality>,
        val weight: Double,
        val isFunctionWord: Boolean,
    ) {
        fun quality(indexStem: String): MatchQuality? = matches[indexStem]
    }

    /** [totalWeight] — не весь запрос, а его объяснимая часть (см. [reach]). */
    private class Query(val terms: List<QueryTerm>, val totalWeight: Double)

    /** Группа выдачи и доля запроса, которую она объясняет (в весах слов). */
    private class Scored(val group: SearchGroup, val covered: Double)

    private fun group(topic: PreparedTopic, query: Query): Scored? {
        val topicScore = score(topic.fields, query)

        val matched = topic.microtopics
            .map { microtopic -> microtopic to score(microtopic.fields, query) }
            .filter { (_, score) -> score > 0 }

        if (matched.isEmpty() && topicScore == 0.0) return null

        val best = matched.maxOfOrNull { it.second } ?: 0.0
        val topicTerms = query.terms.filter { term -> topic.stems.any { term.quality(it) != null } }
        val tail = best * MICROTOPIC_NOISE_RATIO

        val stems = topic.stems + matched.flatMap { (microtopic, _) -> microtopic.stems }
        val covered = query.terms
            .filter { term -> stems.any { term.quality(it) != null } }
            .sumOf { it.weight }

        val group = SearchGroup(
            topic = topic.toSummary(),
            sectionTitle = topic.source.sectionTitle,
            microtopics = matched
                .filter { (microtopic, score) -> score >= floor(microtopic, query, topicTerms, topicScore, tail) }
                .sortedWith(
                    compareByDescending<Pair<PreparedMicrotopic, Double>> { it.second }
                        .thenBy { it.first.source.order }
                )
                .map { (microtopic, _) -> microtopic.source.toSummary() },
            // Группу представляет её сильнейшее совпадение; второе добавляет немного сверху, чтобы
            // тема, совпавшая и сама, и микротемой, обходила тему с одним случайным попаданием.
            score = (maxOf(topicScore, best) + GROUP_SUPPORT * minOf(topicScore, best)).roundToInt(),
        )
        return Scored(group, covered)
    }

    /**
     * Планка, которую микротема обязана взять, чтобы попасть под шапку своей темы.
     *
     * Микротема, объясняющая то, чего в самой теме нет («прошедшее время **отрицание**»), уточняет
     * ответ — с неё спрос только как с хвоста выдачи. Микротема, которая про то же самое, что и
     * тема, должна тему перебить: иначе запрос «наречия» развернул бы все пять её микротем,
     * у каждой из которых это слово в названии, вместо того чтобы ответить самой темой.
     */
    private fun floor(
        microtopic: PreparedMicrotopic,
        query: Query,
        topicTerms: List<QueryTerm>,
        topicScore: Double,
        tail: Double,
    ): Double {
        val refines = query.terms.any { term ->
            term !in topicTerms && microtopic.stems.any { term.quality(it) != null }
        }
        return if (refines) tail else maxOf(tail, topicScore)
    }

    /** Очки объекта: лучшее его поле плюс небольшая добавка за остальные сработавшие. */
    private fun score(fields: List<SearchField>, query: Query): Double {
        val scores = fields.map { field -> fieldScore(field, query) }
        val best = scores.maxOrNull() ?: return 0.0
        return best + FIELD_SUPPORT * (scores.sum() - best)
    }

    private fun fieldScore(field: SearchField, query: Query): Double {
        // Слово запроса засчитывается один раз, по лучшему своему совпадению в поле: повтор слова
        // в длинном названии не должен стоить дороже, чем попадание в короткий тег.
        val matchedWeight = query.terms.sumOf { term ->
            val quality = field.stems.mapNotNull(term::quality).maxByOrNull { it.factor }
            quality?.let { term.weight * it.factor } ?: 0.0
        }
        if (matchedWeight == 0.0) return 0.0

        val matchedStems = field.stems.count { stem -> query.terms.any { it.quality(stem) != null } }
        val precision = matchedStems.toDouble() / field.stems.size
        val start = field.stems.first().let { first -> query.terms.any { it.quality(first) != null } }

        return field.kind.boost() *
            (matchedWeight / query.totalWeight) *
            (PRECISION_FLOOR + (1 - PRECISION_FLOOR) * precision) *
            (if (start) START_BONUS else 1.0)
    }

    private fun SearchFieldKind.boost(): Double = when (this) {
        SearchFieldKind.MICROTOPIC_TITLE -> Weights.MICROTOPIC_TITLE
        SearchFieldKind.TOPIC_TITLE -> Weights.TOPIC_TITLE
        SearchFieldKind.MICROTOPIC_TAG -> Weights.MICROTOPIC_TAG
        SearchFieldKind.TOPIC_TAG -> Weights.TOPIC_TAG
        SearchFieldKind.CARD_TITLE -> Weights.CARD_TITLE
    }

    private fun PreparedTopic.toSummary() = TopicSummary(
        id = source.id,
        title = source.title,
        description = source.description,
        isPretopic = false,
        completedMicrotopics = source.completedMicrotopics,
        totalMicrotopics = microtopics.size,
    )

    private fun IndexedMicrotopic.toSummary() = MicrotopicSummary(
        id = id,
        title = title,
        state = if (isCompleted) MicrotopicState.COMPLETED else MicrotopicState.AVAILABLE,
    )

    /**
     * Вес поля — во сколько оценивается полное попадание в него. Название микротемы дороже
     * названия темы, тег дешевле названия, название карточки — самый слабый сигнал: оно попадает
     * в индекс заодно, а тег писался специально под поиск.
     */
    object Weights {
        const val MICROTOPIC_TITLE = 100.0
        const val TOPIC_TITLE = 90.0
        const val MICROTOPIC_TAG = 60.0
        const val TOPIC_TAG = 55.0
        const val CARD_TITLE = 35.0
    }

    private companion object {
        /** Доля очков поля, которая достаётся ему даже при попадании одним словом из многих. */
        const val PRECISION_FLOOR = 0.45

        /** Совпадение с первого слова строки — это обычно и есть то, что искали. */
        const val START_BONUS = 1.2

        /** Вклад второго и следующих сработавших полей объекта. */
        const val FIELD_SUPPORT = 0.2

        /** Вклад более слабой половины пары «тема ↔ её лучшая микротема» в очки группы. */
        const val GROUP_SUPPORT = 0.25

        /** Насколько группа может объяснять запрос хуже лидера, чтобы вообще попасть в выдачу. */
        const val COVERAGE_TIER_RATIO = 0.6

        /** Доля от лучшего результата, ниже которой совпадение считается хвостом. */
        const val MICROTOPIC_NOISE_RATIO = 0.4
        const val GROUP_NOISE_RATIO = 0.4

        /**
         * Абсолютный пол выдачи: примерно пятая часть от идеального попадания в название
         * ([Weights]). Слабее — это уже отголосок одного случайного слова, а не ответ, и лидер
         * тут ни при чём: на невнятном запросе слабы будут все.
         */
        const val MIN_GROUP_SCORE = 20

        /** Предлоги и союзы ничего не называют, но и выбрасывать их незачем — просто дёшевы. */
        const val FUNCTION_WORD_WEIGHT = 0.1

        /** Даже самое частое слово курса весит не ноль: иначе запрос из одних общих слов пропал бы. */
        const val MIN_TERM_WEIGHT = 0.05
    }
}
