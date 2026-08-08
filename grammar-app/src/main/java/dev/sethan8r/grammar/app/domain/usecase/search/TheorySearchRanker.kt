package dev.sethan8r.grammar.app.domain.usecase.search

import dev.sethan8r.grammar.app.domain.model.theory.IndexedMicrotopic
import dev.sethan8r.grammar.app.domain.model.theory.IndexedTopic
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
 * Модель та же, что в полноценных поисковых движках, только без индекса: слово запроса даёт очки
 * по полю, в котором нашлось (название дороже тега, тег дороже карточки), очко умножается на
 * качество совпадения (точное / дописывается / опечатка), а сумма скрытых сигналов ограничена
 * потолком — чтобы десяток слабых совпадений не обогнал одно сильное.
 *
 * Веса и правила — search_feature_brief.md §8.3.3–§8.3.4 и §9.5.
 */
class TheorySearchRanker @Inject constructor(
    private val normalizer: SearchNormalizer,
) {

    fun rank(index: SearchIndex, query: String): List<SearchGroup> {
        val stems = normalizer.stems(query)
        if (stems.isEmpty()) return emptyList()
        val context = QueryContext(stems, StemCache(normalizer), normalizer)

        // Сперва ищем то, где сошёлся весь запрос. Пусто — ослабляем требование по слову за раз,
        // но не ниже половины: «когда ставить s» лучше показать по «когда», чем не показать вовсе.
        // Ниже половины начинается мусор, поэтому там останавливаемся.
        val minimum = (context.gate.size + 1) / 2
        for (required in context.gate.size downTo minimum) {
            val groups = index.topics
                .mapNotNull { topic -> group(topic, context, required) }
                .sortedWith(compareByDescending<SearchGroup> { it.score }.thenBy { it.topic.id })
            if (groups.isNotEmpty()) return groups.dropNoise()
        }
        return emptyList()
    }

    /**
     * Разобранный запрос. Последнее слово матчится префиксно — пользователь ещё печатает,
     * и «отриц» обязано находить «отрицание» до того, как он допишет.
     */
    private class QueryContext(
        val stems: List<String>,
        val cache: StemCache,
        normalizer: SearchNormalizer,
    ) {
        /** Слова, которые обязаны найтись: предлоги и частицы в заголовки не попадают. */
        val gate: List<String> = stems.filterNot(normalizer::isFunctionWord).ifEmpty { stems }

        fun isLast(stem: String): Boolean = stem == stems.last()
    }

    /** Стемминг одной и той же строки повторяется десятки раз за запрос — считаем однажды. */
    private class StemCache(private val normalizer: SearchNormalizer) {
        private val cache = HashMap<String, List<String>>()
        operator fun get(text: String): List<String> = cache.getOrPut(text) { normalizer.stems(text) }
    }

    private fun group(topic: IndexedTopic, context: QueryContext, required: Int): SearchGroup? {
        val topicFields = fieldStems(context, listOf(topic.title) + topic.keywords)
        val topicScore = scoreTopic(topic, context)

        val matched = topic.microtopics.mapNotNull { microtopic ->
            val ownFields = fieldStems(
                context,
                listOf(microtopic.title) + microtopic.keywords + microtopic.cardTitles,
            )
            // Слова запроса ищутся по теме и микротеме вместе, но покрытие одними полями ТЕМЫ
            // микротему не пускает: иначе «past simple» развернуло бы всю тему простынёй (§8.2.3).
            val covered = context.gate.count { stem ->
                covers(context, stem, topicFields + ownFields)
            } >= required
            val hasOwnHit = context.gate.any { stem -> covers(context, stem, ownFields) }
            if (covered && hasOwnHit) microtopic to scoreMicrotopic(microtopic, context) else null
        }

        val topicItselfMatched =
            context.gate.count { stem -> covers(context, stem, topicFields) } >= required
        if (matched.isEmpty() && !topicItselfMatched) return null

        val best = matched.maxOfOrNull { it.second } ?: 0
        // Запрос про тему целиком («прошедшее время») — ответ это сама тема, а не всё, что под ней
        // как-то перекликается. Разворачиваем только микротемы с сильным собственным попаданием.
        val floor = if (topicItselfMatched) {
            maxOf(best * MICROTOPIC_NOISE_RATIO, STRONG_MICROTOPIC_SCORE.toDouble())
        } else {
            best * MICROTOPIC_NOISE_RATIO
        }
        return SearchGroup(
            topic = TopicSummary(
                id = topic.id,
                title = topic.title,
                description = topic.description,
                isPretopic = false,
                completedMicrotopics = topic.completedMicrotopics,
                totalMicrotopics = topic.microtopics.size,
            ),
            sectionTitle = topic.sectionTitle,
            microtopics = matched
                .filter { (_, score) -> score >= floor }
                .sortedWith(
                    compareByDescending<Pair<IndexedMicrotopic, Int>> { it.second }
                        .thenBy { it.first.order }
                )
                .map { (microtopic, _) -> microtopic.toSummary() },
            // Совпавшая тема и её лучшая микротема складываются: запрос «past simple» должен
            // поднимать саму тему, а не чужую, где эти слова попали в название микротемы.
            score = topicScore + best,
        )
    }

    /** Хвост слабых групп — это совпадения в одном названии карточки; выдачу они только зашумляют. */
    private fun List<SearchGroup>.dropNoise(): List<SearchGroup> {
        val best = firstOrNull()?.score ?: return this
        return filter { it.score >= best * GROUP_NOISE_RATIO }
    }

    private fun scoreTopic(topic: IndexedTopic, context: QueryContext): Int {
        val title = titleScore(
            topic.title, context, TOPIC_TITLE_EXACT, TOPIC_TITLE_START, TOPIC_TITLE_INSIDE
        )
        val tags = topic.keywords.sumOf { tag ->
            tagScore(tag, context, TOPIC_TAG_FULL, TOPIC_TAG_PARTIAL)
        }
        return title + tags.coerceAtMost(HIDDEN_SIGNALS_CAP)
    }

    private fun scoreMicrotopic(microtopic: IndexedMicrotopic, context: QueryContext): Int {
        val title = titleScore(
            microtopic.title, context, MICROTOPIC_TITLE_EXACT, MICROTOPIC_TITLE_START,
            MICROTOPIC_TITLE_INSIDE,
        )
        val tags = microtopic.keywords.sumOf { tag ->
            tagScore(tag, context, MICROTOPIC_TAG_FULL, MICROTOPIC_TAG_PARTIAL)
        }
        val cards = microtopic.cardTitles.sumOf { cardTitle ->
            val stems = context.cache[cardTitle]
            val hits = context.stems.count { stem -> covers(context, stem, stems) }
            if (hits > 0) CARD_TITLE else 0
        }
        return title + (tags + cards).coerceAtMost(HIDDEN_SIGNALS_CAP)
    }

    /**
     * Очки за название. Двойное имя «EN · RU» меряется по каждой части отдельно, иначе русские
     * запросы систематически проигрывали бы английским (§8.7). Запрос, совпавший с частью
     * целиком, — это ровно то, что искал человек, поэтому стоит кратно дороже вхождения.
     */
    private fun titleScore(
        title: String,
        context: QueryContext,
        exact: Int,
        start: Int,
        inside: Int,
    ): Int = title.split(TITLE_SEPARATOR).maxOf { part ->
        val partStems = context.cache[part]
        if (partStems.isEmpty()) return@maxOf 0

        val fullMatch = partStems.size == context.stems.size &&
            partStems.zip(context.stems).all { (indexed, queried) ->
                normalizer.match(queried, indexed, allowPrefix = context.isLast(queried)) != null
            }
        if (fullMatch) return@maxOf exact

        context.stems.sumOf { stem ->
            val quality = partStems.firstNotNullOfOrNull { indexed ->
                normalizer.match(stem, indexed, allowPrefix = context.isLast(stem))
            } ?: return@sumOf 0
            val weight = if (matchesFirstWord(stem, partStems, context)) start else inside
            (weight * quality.factor).roundToInt()
        }
    }

    private fun matchesFirstWord(
        stem: String,
        partStems: List<String>,
        context: QueryContext,
    ): Boolean =
        normalizer.match(stem, partStems.first(), allowPrefix = context.isLast(stem)) != null

    /**
     * Очки за тег. Тег пишется готовой фразой пользователя, поэтому запрос, покрывающий тег
     * целиком, почти равен попаданию в заголовок; частичное совпадение — вспомогательный сигнал.
     */
    private fun tagScore(tag: String, context: QueryContext, full: Int, partial: Int): Int {
        val tagStems = context.cache[tag]
        if (tagStems.isEmpty()) return 0
        val covered = tagStems.count { tagStem ->
            context.stems.any { normalizer.match(it, tagStem, allowPrefix = context.isLast(it)) != null }
        }
        return when {
            covered == 0 -> 0
            covered == tagStems.size -> full
            else -> partial
        }
    }

    private fun covers(context: QueryContext, stem: String, fieldStems: Collection<String>): Boolean =
        fieldStems.any { normalizer.match(stem, it, allowPrefix = context.isLast(stem)) != null }

    private fun fieldStems(context: QueryContext, fields: List<String>): Set<String> =
        fields.flatMapTo(mutableSetOf()) { context.cache[it] }

    private fun IndexedMicrotopic.toSummary() = MicrotopicSummary(
        id = id,
        title = title,
        state = if (isCompleted) MicrotopicState.COMPLETED else MicrotopicState.AVAILABLE,
    )

    companion object {
        const val MICROTOPIC_TITLE_EXACT = 220
        const val MICROTOPIC_TITLE_START = 100
        const val MICROTOPIC_TITLE_INSIDE = 70
        const val TOPIC_TITLE_EXACT = 200
        const val TOPIC_TITLE_START = 60
        const val TOPIC_TITLE_INSIDE = 45
        const val MICROTOPIC_TAG_FULL = 40
        const val TOPIC_TAG_FULL = 25
        const val MICROTOPIC_TAG_PARTIAL = 15
        const val CARD_TITLE = 15

        // Дороже названия карточки: тег писался специально под поиск, а карточка попадает
        // в индекс заодно. Иначе запрос «простое» ставил бы чужую тему с подходящей карточкой
        // выше самой темы Past Simple.
        const val TOPIC_TAG_PARTIAL = 20

        /** Никакое количество тегов и карточек не обгоняет прямое попадание в название микротемы. */
        const val HIDDEN_SIGNALS_CAP = 60

        /** Доля от лучшего результата, ниже которой совпадение считается шумом. */
        const val MICROTOPIC_NOISE_RATIO = 0.4
        const val GROUP_NOISE_RATIO = 0.3

        /** Планка «сильного» совпадения микротемы — вхождение в её название, а не отголосок в тегах. */
        const val STRONG_MICROTOPIC_SCORE = MICROTOPIC_TITLE_INSIDE

        private const val TITLE_SEPARATOR = " · "
    }
}
