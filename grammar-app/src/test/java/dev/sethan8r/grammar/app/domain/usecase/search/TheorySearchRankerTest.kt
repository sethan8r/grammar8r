package dev.sethan8r.grammar.app.domain.usecase.search

import dev.sethan8r.grammar.app.domain.model.theory.IndexedMicrotopic
import dev.sethan8r.grammar.app.domain.model.theory.IndexedTopic
import dev.sethan8r.grammar.app.domain.model.theory.SearchIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Правила ранжирования на выдуманном индексе — без content.db и без Android. Здесь проверяется
 * сама механика; как она ведёт себя на настоящем курсе — в `TheorySearchProtocolTest`.
 */
class TheorySearchRankerTest {

    private val ranker = TheorySearchRanker(SearchNormalizer())

    @Test
    fun `запрос про тему отвечается темой, а не её оглавлением`() {
        val index = prepare(
            topic(
                id = 1,
                title = "Наречия",
                microtopics = listOf(
                    microtopic(11, "Adverbs of Place · Наречия места"),
                    microtopic(12, "Adverbs of Time · Наречия времени"),
                ),
            ),
        )

        val group = ranker.rank(index, "наречия").single()

        assertTrue(group.microtopics.isEmpty(), "тема развернулась: ${group.microtopics}")
    }

    @Test
    fun `микротема разворачивается, когда уточняет запрос`() {
        val index = prepare(
            topic(
                id = 1,
                title = "Наречия",
                microtopics = listOf(
                    microtopic(11, "Adverbs of Place · Наречия места"),
                    microtopic(12, "Adverbs of Time · Наречия времени"),
                ),
            ),
        )

        val group = ranker.rank(index, "наречия места").single()

        assertEquals(listOf("Adverbs of Place · Наречия места"), group.microtopics.map { it.title })
    }

    @Test
    fun `редкое слово запроса весомее частого`() {
        val rare = topic(
            id = 1,
            title = "Части речи",
            microtopics = listOf(microtopic(11, "Subject · Подлежащее")),
        )
        val common = (2..6).map { id ->
            topic(
                id = id,
                title = "Тема $id",
                microtopics = listOf(microtopic(id * 10, "Verb $id · Глагол $id")),
            )
        }
        val index = prepare(rare, *common.toTypedArray())

        val groups = ranker.rank(index, "глагол подлежащее")

        assertEquals("Части речи", groups.first().topic.title, "выдача: ${groups.map { it.topic.title }}")
    }

    @Test
    fun `слово, которого нет в курсе, не обнуляет выдачу`() {
        val index = prepare(
            topic(
                id = 1,
                title = "Части речи",
                microtopics = listOf(microtopic(11, "Subject · Подлежащее")),
            ),
        )

        val groups = ranker.rank(index, "почему подлежащее абракадабра")

        assertEquals(listOf("Части речи"), groups.map { it.topic.title })
    }

    @Test
    fun `тег вытаскивает микротему, которой нет в заголовках`() {
        val index = prepare(
            topic(
                id = 1,
                title = "Past Simple",
                microtopics = listOf(
                    microtopic(11, "used to · Раньше было", keywords = listOf("прошлая привычка")),
                    microtopic(12, "Time markers · Маркеры"),
                ),
            ),
        )

        val group = ranker.rank(index, "прошлая привычка").single()

        assertEquals(listOf("used to · Раньше было"), group.microtopics.map { it.title })
    }

    private fun prepare(vararg topics: IndexedTopic) = ranker.prepare(SearchIndex(topics.toList()))

    private fun topic(
        id: Int,
        title: String,
        keywords: List<String> = emptyList(),
        microtopics: List<IndexedMicrotopic>,
    ) = IndexedTopic(
        id = id,
        title = title,
        keywords = keywords,
        description = null,
        sectionTitle = null,
        order = id,
        completedMicrotopics = 0,
        microtopics = microtopics,
    )

    private fun microtopic(
        id: Int,
        title: String,
        keywords: List<String> = emptyList(),
        cardTitles: List<String> = emptyList(),
    ) = IndexedMicrotopic(
        id = id,
        title = title,
        keywords = keywords,
        cardTitles = cardTitles,
        order = id,
        isCompleted = false,
    )
}
