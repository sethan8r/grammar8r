package dev.sethan8r.grammar.app.ui.components.exercise.parts

import kotlin.random.Random

/**
 * Раскладывает элементы пула по строкам из [perRow] ячеек так, чтобы в строке стояли элементы одной
 * высоты и рядом с коротким чипом не оставалось пустоты. Ровные строки идут сверху в случайном порядке
 * [random]. Элементы, которым не нашлось пары той же высоты, собираются в строки из ближайших по высоте
 * и уходят в самый низ; неполная строка — последней. Внутри группы одной высоты порядок [items] сохраняется.
 *
 * Возвращает плоский список: `result.chunked(perRow)` даёт строки сверху вниз.
 */
internal fun <T> packPoolRows(
    items: List<T>,
    perRow: Int,
    heightOf: (T) -> Int,
    random: Random,
): List<T> {
    require(perRow > 0) { "perRow must be positive: $perRow" }
    val evenRows = mutableListOf<List<T>>()
    val leftovers = mutableListOf<T>()
    items.groupBy(heightOf).values.forEach { group ->
        val fullCount = group.size - group.size % perRow
        evenRows += group.subList(0, fullCount).chunked(perRow)
        leftovers += group.subList(fullCount, group.size)
    }
    val mixedRows = leftovers.sortedBy(heightOf).chunked(perRow)
    return (evenRows.shuffled(random) + mixedRows).flatten()
}