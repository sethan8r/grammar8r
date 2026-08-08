package dev.sethan8r.grammar.app.domain.usecase.search

import dev.sethan8r.grammar.app.domain.model.theory.SearchGroup
import dev.sethan8r.grammar.app.domain.model.theory.SearchIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Протокол пилота (search_feature_brief.md §8.6) на настоящей content.db: те же запросы, что
 * пользователь вбивает на устройстве. Печатает выдачу с очками — таблица «запрос → результат»
 * берётся прямо из отчёта прогона.
 *
 * Теги проставлены только у Past Simple и Present Simple; остальные темы участвуют как есть —
 * это часть теста (микротемы без тегов обязаны находиться по заголовкам).
 */
class TheorySearchProtocolTest {

    private val index: SearchIndex = ContentDbIndexLoader.load()
    private val ranker = TheorySearchRanker(SearchNormalizer())

    private fun search(query: String): List<SearchGroup> =
        ranker.rank(index, query).also { report(query, it) }

    private fun report(query: String, groups: List<SearchGroup>) {
        println("\n=== «$query» → ${groups.size} групп")
        groups.forEach { group ->
            println("  [${group.score}] ${group.topic.title}" + (group.sectionTitle?.let { " ($it)" } ?: ""))
            group.microtopics.forEach { println("        • ${it.title}") }
        }
    }

    private fun SearchGroup.microtopicTitles(): List<String> = microtopics.map { it.title }

    /**
     * Отчётный прогон: запросы так, как их напишет живой человек. Ассертов нет намеренно —
     * выдача печатается и вычитывается глазами, тут ловятся «странности», которых не видно
     * из отдельных правил.
     */
    @Test
    fun `человеческие запросы — отчёт`() {
        listOf(
            "прошедшее время", "как сказать не делал", "вопросы в прошлом", "неправильные глаголы",
            "was were", "окончание ed", "маркеры", "used to", "раньше делал", "настоящее время",
            "когда ставить s", "расписание", "как часто", "have to", "должен", "отриц",
            "прошедш", "did", "вопрос",
        ).forEach { search(it) }
    }

    // 1 — базовое совпадение по названию микротемы, конкуренция двух групп
    @Test
    fun `отрицание находит обе темы`() {
        val groups = search("отрицание")
        val titles = groups.map { it.topic.title }
        assertTrue("Past Simple" in titles, "нет Past Simple")
        assertTrue("Present Simple" in titles, "нет Present Simple")
        groups.filter { it.topic.title.endsWith("Simple") }.forEach { group ->
            assertTrue(
                group.microtopicTitles().any { it.contains("отрицание", ignoreCase = true) },
                "в группе ${group.topic.title} нет микротемы отрицания",
            )
        }
    }

    // 2 — гейт по объединению полей: слова лежат в названии микротемы и в теге темы
    @Test
    fun `прошедшее время отрицание не тянет Present Simple`() {
        val groups = search("прошедшее время отрицание")
        assertEquals(listOf("Past Simple"), groups.map { it.topic.title })
        assertTrue(groups.single().microtopicTitles().any { it.contains("отрицание", ignoreCase = true) })
    }

    // 3 — тема не разворачивает все свои микротемы
    @Test
    fun `past simple не разворачивает всю тему`() {
        val group = search("past simple").first { it.topic.title == "Past Simple" }
        assertTrue(
            group.microtopics.size < group.topic.totalMicrotopics,
            "тема развернулась простынёй: ${group.microtopics.size} микротем",
        )
    }

    // 4, 5 — усечение основ: число, падеж, лишняя буква
    @Test
    fun `формы слова и опечатка в хвосте не меняют выдачу`() {
        val expected = search("прошедшее время").map { it.topic.title }
        assertEquals(expected, search("прошедшие времена").map { it.topic.title })
        assertEquals(expected, search("прошедьшее время").map { it.topic.title })
    }

    // 6 — усечение основ на двух словах сразу
    @Test
    fun `маркеры прошлое находит маркеры прошлого`() {
        val groups = search("маркеры прошлое")
        assertTrue(groups.first().microtopicTitles().any { it.startsWith("Time markers") })
    }

    // 7 — тег, покрытый целиком, вытаскивает микротему наверх своей группы
    @Test
    fun `прошлая привычка поднимает used to`() {
        val group = search("прошлая привычка").first()
        assertTrue(
            group.microtopicTitles().first().startsWith("used to"),
            "used to не первая: ${group.microtopicTitles()}",
        )
    }

    // Предлог, которого нет ни в одном заголовке, не должен обнулять выдачу
    @Test
    fun `служебное слово в запросе не ломает поиск`() {
        val withPreposition = search("привычки в прошлом").map { it.topic.title }
        assertTrue(withPreposition.isNotEmpty(), "запрос с предлогом ничего не нашёл")
        assertEquals(search("привычки прошлом").map { it.topic.title }, withPreposition)
    }

    // 8 — заголовок бьёт тег
    @Test
    fun `попадание в заголовок стоит дороже тега`() {
        val byTitle = search("used to").first { it.topic.title == "Past Simple" }.score
        val byTag = search("прошлая привычка").first { it.topic.title == "Past Simple" }.score
        assertTrue(byTitle > byTag, "заголовок ($byTitle) не обогнал тег ($byTag)")
    }

    // 9 — название карточки как скрытый сигнал
    @Test
    fun `запрос по названию карточки находит её микротему`() {
        // «ago» есть только в названии карточки 249 «Маркеры прошлого: словарь и слово ago».
        val groups = search("слово ago")
        assertTrue(
            groups.first().microtopicTitles().any { it.startsWith("Time markers") },
            "микротема карточки не найдена: ${groups.firstOrNull()?.microtopicTitles()}",
        )
    }

    // 10 — пустая выдача
    @Test
    fun `бессмысленный запрос не находит ничего`() {
        assertTrue(search("абракадабра").isEmpty())
    }

    // 12 — темы без тегов продолжают находиться по заголовку
    @Test
    fun `тема без тегов находится по заголовку`() {
        val groups = search("транскрипция")
        assertTrue(groups.any { it.topic.title == "Транскрипция" }, "тема без тегов потерялась")
    }

    // Требование пользователя: варианты написания didn't
    @Test
    fun `все написания didn't находят микротему отрицания`() {
        for (query in listOf("didn't", "didnt", "didn t", "did not")) {
            val groups = search(query)
            assertTrue(
                groups.any { group -> group.microtopicTitles().any { "didn't" in it } },
                "по запросу «$query» микротема с didn't не найдена",
            )
        }
    }

    // Потолок скрытых сигналов: никакой набор тегов не обгоняет прямой заголовок
    @Test
    fun `скрытые сигналы не обгоняют название микротемы`() {
        val groups = ranker.rank(index, "указатели времени")
        val markers = groups
            .flatMap { group -> group.microtopics.map { group to it } }
            .firstOrNull { (_, microtopic) -> microtopic.title.startsWith("Time markers") }
        assertTrue(markers != null, "микротема маркеров не найдена")
    }
}
