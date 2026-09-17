package dev.sethan8r.grammar.core.usecase.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** Стемминг: формы одного слова должны сходиться, разные слова — расходиться. */
class WordStemmerTest {

    private fun stem(word: String) = WordStemmer.stem(word)

    @Test
    fun `формы одного слова дают одну основу`() {
        assertEquals(stem("прошедшее"), stem("прошедшие"))
        assertEquals(stem("прошлое"), stem("прошлого"))
        assertEquals(stem("прошлое"), stem("прошлая"))
        assertEquals(stem("привычка"), stem("привычки"))
        assertEquals(stem("отрицание"), stem("отрицания"))
        assertEquals(stem("указатели"), stem("указатель"))
        assertEquals(stem("маркеры"), stem("маркер"))
        assertEquals(stem("вопрос"), stem("вопросы"))
    }

    @Test
    fun `разные слова с общим началом не сливаются`() {
        assertNotEquals(stem("прошедшее"), stem("прошлое"))
        assertNotEquals(stem("частота"), stem("часть"))
        assertNotEquals(stem("глагол"), stem("гладкий"))
    }

    @Test
    fun `множественное число в английском снимается`() {
        assertEquals(stem("marker"), stem("markers"))
        assertEquals(stem("question"), stem("questions"))
        assertEquals(stem("verb"), stem("verbs"))
    }

    @Test
    fun `английские формы курса не калечатся`() {
        // -ed и -ing здесь предмет изучения, а не словоформа: used to обязано остаться used to.
        assertEquals("used", stem("used"))
        assertEquals("simple", stem("simple"))
        assertEquals("continuous", stem("continuous"))
        assertEquals("was", stem("was"))
    }
}
