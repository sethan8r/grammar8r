package dev.sethan8r.grammar.app.domain.usecase.search

import dev.sethan8r.grammar.app.domain.model.theory.IndexedMicrotopic
import dev.sethan8r.grammar.app.domain.model.theory.IndexedTopic
import dev.sethan8r.grammar.app.domain.model.theory.SearchIndex
import kotlin.math.ln

/** Откуда пришла строка индекса — от неё зависит вес совпадения (веса живут в ранкере). */
enum class SearchFieldKind {
    TOPIC_TITLE,
    TOPIC_TAG,
    MICROTOPIC_TITLE,
    MICROTOPIC_TAG,
    CARD_TITLE,
}

/**
 * Одна строка индекса, уже разобранная на основы. Двойное название «EN · RU» кладётся двумя
 * полями: части меряются порознь, иначе русские запросы систематически проигрывали бы английским.
 */
class SearchField(val kind: SearchFieldKind, val stems: List<String>)

class PreparedMicrotopic(val source: IndexedMicrotopic, val fields: List<SearchField>) {
    val stems: Set<String> = fields.flatMapTo(HashSet()) { it.stems }
}

class PreparedTopic(
    val source: IndexedTopic,
    val fields: List<SearchField>,
    val microtopics: List<PreparedMicrotopic>,
) {
    val stems: Set<String> = fields.flatMapTo(HashSet()) { it.stems }
}

/**
 * Снимок курса, подготовленный к поиску: все строки разобраны на основы один раз, и посчитано,
 * в скольких объектах каждая основа встречается. Частотность нужна, чтобы взвешивать слова
 * запроса: `подлежащее` есть в одном теге на весь курс и почти однозначно называет цель, а
 * `нужно` или `всегда` встречаются повсюду и не различают ничего. Без этого длинная человеческая
 * фраза тонула в словах-связках.
 *
 * Готовится на каждый снимок индекса, а не на каждое нажатие клавиши — стеммить весь курс
 * посимвольно незачем.
 */
class PreparedIndex private constructor(
    val topics: List<PreparedTopic>,
    private val documentFrequency: Map<String, Int>,
    private val documentCount: Int,
) {

    private val maxIdf = idf(1)

    /**
     * Слова индекса, с которыми совпало слово запроса, и качество каждого совпадения.
     * Пусто — такого слова в курсе нет вообще: оно ничего не различает и в ранжировании
     * не участвует (иначе лишнее слово в вопросе обнуляло бы всю выдачу).
     */
    fun resolve(queryStem: String, allowPrefix: Boolean, normalizer: SearchNormalizer): Match =
        documentFrequency.keys
            .mapNotNull { indexStem ->
                normalizer.match(queryStem, indexStem, allowPrefix)?.let { indexStem to it }
            }
            .toMap()
            .let { matches ->
                Match(
                    stems = matches,
                    // Сколько объектов затронуто: точного объединения постингов не строим —
                    // на нашем объёме самой частой из совпавших основ достаточно.
                    documentFrequency = matches.keys.maxOfOrNull { documentFrequency.getValue(it) } ?: 0,
                )
            }

    /** Вес слова: 1.0 у слова из одного объекта, ~0 у слова, встречающегося по всему курсу. */
    fun weight(documentFrequency: Int): Double = (idf(documentFrequency) / maxIdf).coerceIn(0.0, 1.0)

    private fun idf(documentFrequency: Int): Double =
        ln((documentCount + 1.0) / (documentFrequency + 1.0))

    class Match(val stems: Map<String, MatchQuality>, val documentFrequency: Int)

    companion object {

        fun of(index: SearchIndex, normalizer: SearchNormalizer): PreparedIndex {
            val cache = HashMap<String, List<String>>()
            fun stems(text: String): List<String> = cache.getOrPut(text) { normalizer.stems(text) }

            fun fields(kind: SearchFieldKind, texts: List<String>): List<SearchField> = texts
                .flatMap { it.split(TITLE_SEPARATOR) }
                .map { SearchField(kind, stems(it)) }
                .filter { it.stems.isNotEmpty() }

            val topics = index.topics.map { topic ->
                PreparedTopic(
                    source = topic,
                    fields = fields(SearchFieldKind.TOPIC_TITLE, listOf(topic.title)) +
                        fields(SearchFieldKind.TOPIC_TAG, topic.keywords),
                    microtopics = topic.microtopics.map { microtopic ->
                        PreparedMicrotopic(
                            source = microtopic,
                            fields = fields(SearchFieldKind.MICROTOPIC_TITLE, listOf(microtopic.title)) +
                                fields(SearchFieldKind.MICROTOPIC_TAG, microtopic.keywords) +
                                fields(SearchFieldKind.CARD_TITLE, microtopic.cardTitles),
                        )
                    },
                )
            }

            // Документ — тема или микротема со своими полями: частотность должна отражать, во
            // скольких РАЗНЫХ местах курса слово встречается, а не сколько раз оно написано.
            val documents = topics.map { it.stems } + topics.flatMap { it.microtopics.map { m -> m.stems } }
            val frequency = HashMap<String, Int>()
            documents.forEach { document -> document.forEach { stem -> frequency.merge(stem, 1, Int::plus) } }

            return PreparedIndex(topics, frequency, documents.size)
        }

        private const val TITLE_SEPARATOR = " · "
    }
}
