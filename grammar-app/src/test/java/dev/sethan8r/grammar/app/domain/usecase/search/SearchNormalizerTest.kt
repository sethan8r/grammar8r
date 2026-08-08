package dev.sethan8r.grammar.app.domain.usecase.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Нормализация, сокращения и сравнение основ — search_feature_brief.md §8.3.2, §9.5. */
class SearchNormalizerTest {

    private val normalizer = SearchNormalizer()

    /** Все значимые слова запроса нашлись в строке индекса. */
    private fun matches(query: String, indexed: String): Boolean {
        val fieldStems = normalizer.stems(indexed)
        val queryStems = normalizer.stems(query)
        return queryStems.all { stem ->
            fieldStems.any { normalizer.match(stem, it, allowPrefix = stem == queryStems.last()) != null }
        }
    }

    @Test
    fun `окончания, падежи и число не мешают`() {
        assertTrue(matches("прошедшие времена", "прошедшее время"))
        assertTrue(matches("маркеры прошлое", "Маркеры прошлого"))
        assertTrue(matches("привычки прошлом", "прошлая привычка"))
        assertTrue(matches("вопросы", "Questions · Вопрос: Did…?"))
    }

    @Test
    fun `опечатка в одну букву прощается`() {
        assertTrue(matches("прашедшее", "прошедшее время"))
        assertTrue(matches("отрецание", "Отрицание: didn't"))
    }

    @Test
    fun `близкие по началу слова НЕ считаются одним`() {
        val past = normalizer.stems("прошедшее").single()
        val previous = normalizer.stems("прошлое").single()
        assertNull(
            normalizer.match(past, previous, allowPrefix = false),
            "«прошедшее» и «прошлое» не должны совпадать — из-за этого выдача расползалась",
        )
    }

    @Test
    fun `регистр, ё и пунктуация игнорируются`() {
        assertTrue(matches("ВОПРОС DID", "Questions · Вопрос: Did…?"))
        assertEquals(normalizer.words("ещё"), normalizer.words("еще"))
    }

    @Test
    fun `все написания didn't сходятся в одно`() {
        val expected = listOf("did", "not")
        assertEquals(expected, normalizer.words("didn't"))
        assertEquals(expected, normalizer.words("didnt"))
        assertEquals(expected, normalizer.words("didn t"))
        assertEquals(expected, normalizer.words("did not"))
        assertEquals(expected, normalizer.words("didn’t"))
    }

    @Test
    fun `любое написание didn't находит микротему`() {
        val title = "Negative · Отрицание: didn't"
        for (query in listOf("didn't", "didnt", "didn t", "did not", "DIDN'T")) {
            assertTrue(matches(query, title), "не нашлось по запросу «$query»")
        }
    }

    @Test
    fun `остальные сокращения ведут себя так же`() {
        assertEquals(listOf("do", "not"), normalizer.words("don't"))
        assertEquals(listOf("does", "not"), normalizer.words("doesn t"))
        assertEquals(listOf("is", "not"), normalizer.words("isnt"))
        assertEquals(listOf("can", "not"), normalizer.words("can't"))
        assertEquals(listOf("will", "not"), normalizer.words("won't"))
    }

    @Test
    fun `недописанное слово находит целое`() {
        assertNotNull(normalizer.match("отриц", "отрицан", allowPrefix = true))
        assertNull(
            normalizer.match("отриц", "отрицан", allowPrefix = false),
            "префикс разрешён только последнему слову запроса",
        )
    }

    @Test
    fun `короткие слова сравниваются точно`() {
        assertNull(normalizer.match("не", "неправ", allowPrefix = true))
        assertNotNull(normalizer.match("did", "did", allowPrefix = false))
    }

    /**
     * Стоп-слова перечислены основами, а не словами: список сверяется уже после стемминга.
     * Разъезд ловится только так — `сказать` даёт основу `сказ`, и запись `сказа` в наборе
     * молча не срабатывала бы, вытаскивая микротемы со словом «сказать» в названии.
     */
    @Test
    fun `слова-связки не влияют на запрос`() {
        for (glue in listOf("как", "что", "это", "так", "сказать")) {
            assertEquals(
                normalizer.stems("отрицание"),
                normalizer.stems("$glue отрицание"),
                "«$glue» не выброшено из запроса",
            )
        }
    }

    /** Тот же инвариант для предлогов и союзов: они помечаются служебными уже как основы. */
    @Test
    fun `предлоги и союзы распознаются служебными`() {
        for (word in listOf("в", "на", "с", "для", "через", "или", "чтобы", "при", "без")) {
            assertTrue(
                normalizer.stems(word).all(normalizer::isFunctionWord),
                "«$word» не распознан служебным словом",
            )
        }
    }
}
