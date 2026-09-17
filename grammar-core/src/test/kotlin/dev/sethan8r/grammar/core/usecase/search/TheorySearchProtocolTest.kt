package dev.sethan8r.grammar.core.usecase.search

import dev.sethan8r.grammar.core.model.theory.SearchGroup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Протокол пилота (search_feature_brief.md §8.6) на настоящей content.db: те же запросы, что
 * пользователь вбивает на устройстве. Печатает выдачу с очками — таблица «запрос → результат»
 * берётся прямо из отчёта прогона.
 */
class TheorySearchProtocolTest {

    private val ranker = TheorySearchRanker(SearchNormalizer())
    private val index: PreparedIndex = ranker.prepare(ContentDbIndexLoader.load())

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
            "прошедш", "did", "вопрос", "простое", "часы", "длительное",
            "процесс прямо сейчас", "фон и вторжение", "что будет",
            "как связать два предложения", "но и однако", "хотя несмотря на",
            "части речи", "какое это слово существительное",
            "наречия", "окончание ly",
            "do или does", "как задать вопрос did", "вспомогательный глагол", "doesn't и don't",
            "как перевести will", "инфинитив с to", "модальный глагол без to", "would like",
            "как понять нужен ли to после глагола", "страдательный залог", "с кем что-то сделали",
            "made in italy", "меня родили по-английски", "v1 v2 v3", "неправильные глаголы наизусть",
            "правильные глаголы ed", "окончание ed произношение", "go went gone",
            "как сравнить два предмета", "самый лучший из всех", "не такой же как этот",
            "почему в английском так важен порядок слов", "как читать длинное английское предложение",
            "чем английский отличается от русского",
            "как читать транскрипцию словаря", "где ставить ударение в слове", "что за гласные звуки такие",
            "какое время выбрать настоящее", "разница между simple и continuous", "происходит сейчас или всегда",
            "какой предлог после глагола listen", "afraid of или interested in", "предлог прилип к прилагательному",
            "неопределённый артикль", "который час", "как сказать тоже по-английски",
            "чей это", "как задать вопрос словом where", "there is there are",
            "предлог at для времени", "как сказать иметь", "мой велосипед чей",
            "этот или тот", "как сказать разрешение можно", "могу или умею",
            "вежливая просьба please", "давай сделаем вместе", "команда не бегай",
            "какой сегодня день недели", "заглавная буква в месяце", "движение через мост",
            "из коробки наружу", "предлог рядом near", "нельзя посчитать много",
            "почему подлежащее нужно всегда", "холодно на улице безличное", "мне нужно идти",
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

    // 2 — совпадение по объединению полей: слова лежат в названии микротемы и в теге темы.
    // Отвечает ПЕРВАЯ группа: у каждого времени курса есть своя микротема «Отрицание», и они
    // законно цепляются за это слово — проверяем не отсутствие соседей в хвосте, а голову выдачи.
    @Test
    fun `прошедшее время отрицание отвечается отрицанием Past Simple`() {
        val groups = search("прошедшее время отрицание")
        assertEquals("Past Simple", groups.first().topic.title)
        assertTrue(groups.first().microtopicTitles().any { it.contains("отрицание", ignoreCase = true) })
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

    // 4 — усечение основ: другое число и падеж дают ровно ту же выдачу.
    @Test
    fun `формы слова не меняют выдачу`() {
        val expected = search("прошедшее время").map { it.topic.title }
        assertEquals(expected, search("прошедшие времена").map { it.topic.title })
    }

    // 5 — опечатка в хвосте слова. С неё спрос мягче, чем с формы слова: неточное совпадение
    // стоит дешевле точного, поэтому близкие по очкам соседи в хвосте могут поменяться местами.
    // Гарантируем то, что важно пользователю: ответ найден и стоит первым.
    @Test
    fun `опечатка в хвосте не теряет нужную тему`() {
        val groups = search("прошедьшее время")
        assertEquals(search("прошедшее время").first().topic.title, groups.firstOrNull()?.topic?.title)
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

    /**
     * Запрос, описывающий тему целиком, отвечается самой темой: микротемы под шапкой
     * разворачиваются, только если уточняют ответ. Раньше «наречия» показывало все пять микротем
     * темы «Наречия» — у каждой это слово в названии (§10.10, находка о простыне).
     */
    @Test
    fun `запрос про тему не разворачивает её оглавление`() {
        val group = search("наречия").first()
        assertEquals("Наречия", group.topic.title)
        assertTrue(
            group.microtopics.isEmpty(),
            "тема развернулась простынёй: ${group.microtopicTitles()}",
        )
    }

    /**
     * Находки §10.10: человеческие запросы, на которых выдача уезжала мимо цели. Проверяем
     * первую группу — она и есть ответ; хвост допустим.
     */
    @Test
    fun `находки протокола — нужная тема стоит первой`() {
        val expected = mapOf(
            // 1 — тему про прошедшее обгоняла соседняя из-за слова в названии её микротемы
            "прошедшее время" to "Past Simple",
            // 2 — «Связная речь» лезла вперёд на бытовой запрос
            "части речи" to "Части речи",
            // 5 — тема терялась целиком, хотя это её описание
            "чем английский отличается от русского" to "Как думает английский язык",
            // 6 — тема-сравнение не находилась вопросом, ради которого написана
            "разница между simple и continuous" to "Present Simple или Present Continuous",
            // 12 — тег совпадал дословно, а выдача была мимо
            "почему monday с большой буквы" to "Основы",
        )
        for ((query, topic) in expected) {
            assertEquals(topic, search(query).firstOrNull()?.topic?.title, "запрос «$query»")
        }
    }

    /**
     * Находки §10.10 (9–12): длинный запрос со словами-связками вокруг ключевого давал ноль
     * групп, хотя слово из тега совпадало дословно.
     */
    @Test
    fun `длинный бытовой запрос находит свою микротему`() {
        val expected = mapOf(
            "как сказать иметь" to "Have / Has",
            "как сказать разрешение можно" to "Can / can't",
            "почему подлежащее нужно всегда" to "Word Order: SVO",
            "холодно на улице безличное" to "Word Order: SVO",
            "out of или out" to "Direction Prepositions",
            "как понять нужен ли to после глагола" to "Verbs Without to",
        )
        for ((query, microtopic) in expected) {
            val titles = search(query).first().microtopicTitles()
            assertTrue(
                titles.any { it.startsWith(microtopic) },
                "запрос «$query» не нашёл «$microtopic»: $titles",
            )
        }
    }

    /**
     * Запрос из одних предлогов и союзов ничего не называет — выдача пустая, а не весь курс:
     * союз «или» стоит в названиях доброго десятка микротем, и раньше «могу или умею» вытаскивало
     * их все.
     */
    @Test
    fun `запрос из служебных слов ничего не находит`() {
        for (query in listOf("в на с", "или же", "для по от")) {
            assertTrue(search(query).isEmpty(), "запрос «$query» что-то нашёл")
        }
    }
}
