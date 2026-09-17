package dev.sethan8r.grammar.app.ui.components.exercise.parts

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Раскладка пула по высоте чипов и правка сетки после первого касания. */
class PackedPoolTest {

    private fun rows(heights: Map<String, Int>, perRow: Int = 2, seed: Int = 7): List<List<Int>> =
        packPoolRows(heights.keys.toList(), perRow, heights::getValue, Random(seed))
            .chunked(perRow)
            .map { row -> row.map(heights::getValue) }

    @Test
    fun `чипы одной высоты встают в пару, разные — в самый низ`() {
        // Раскладка задания «hear или listen to»: две однострочные, пять двухстрочных, одна трёхстрочная.
        val heights = mapOf(
            "Храп" to 1, "Гром" to 1,
            "Лекция" to 2, "Голосовое" to 2, "Аудиокнига" to 2, "Песня" to 2, "Сирена" to 2,
            "Альбом" to 3,
        )
        val result = rows(heights)

        assertEquals(4, result.size)
        result.dropLast(1).forEach { row -> assertEquals(1, row.toSet().size, "ровная строка: $row") }
        assertEquals(listOf(2, 3), result.last())
    }

    @Test
    fun `остатки собираются из ближайших по высоте, неполная строка последней`() {
        val heights = mapOf("a" to 1, "b" to 3, "c" to 2)

        assertEquals(listOf(listOf(1, 2), listOf(3)), rows(heights))
    }

    @Test
    fun `работает для любого числа ячеек в строке`() {
        val heights = mapOf("a" to 1, "b" to 1, "c" to 1, "d" to 2, "e" to 2)
        val result = rows(heights, perRow = 3)

        assertEquals(listOf(listOf(1, 1, 1), listOf(2, 2)), result)
    }

    @Test
    fun `ничего не теряется и не дублируется`() {
        val heights = (1..15).associate { "item$it" to it % 4 }
        val packed = packPoolRows(heights.keys.toList(), 2, heights::getValue, Random(1))

        assertEquals(heights.keys.sorted(), packed.sorted())
    }

    @Test
    fun `один и тот же seed даёт одну раскладку`() {
        val heights = (1..12).associate { "item$it" to it % 3 }
        val items = heights.keys.toList()

        assertEquals(
            packPoolRows(items, 2, heights::getValue, Random(42)),
            packPoolRows(items, 2, heights::getValue, Random(42)),
        )
    }

    @Test
    fun `пустой пул остаётся пустым`() {
        assertTrue(packPoolRows(emptyList<String>(), 2, { 0 }, Random(0)).isEmpty())
    }

    @Test
    fun `изъятый чип оставляет дыру, соседи не сдвигаются`() {
        val state = PackedPoolState(listOf(1, 2, 3, 4), columns = 2, seed = 0)

        state.take(2)

        assertEquals(listOf(1, null, 3, 4), state.cells)
    }

    @Test
    fun `вернувшийся чип занимает дыру в строке, где ещё есть чипы`() {
        val state = PackedPoolState(listOf(1, 2, 3, 4), columns = 2, seed = 0)
        state.take(1)
        state.take(2)
        state.take(4)

        state.putBack(2)

        assertEquals(listOf(null, null, 3, 2), state.cells)
    }

    @Test
    fun `если все дыры в пустых строках — чип встаёт в конец`() {
        val state = PackedPoolState(listOf(1, 2, 3, 4), columns = 2, seed = 0)
        state.take(1)
        state.take(2)

        state.putBack(1)

        assertEquals(listOf(null, null, 3, 4, 1), state.cells)
    }
}