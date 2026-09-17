package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.random.Random

/**
 * Состояние пула drag-задания — сетка ячеек с дырами. Пока пул не трогали, раскладку задаёт
 * [PackedPool] по измеренной высоте чипов ([packPoolRows]). После первого изъятия сетка фиксируется
 * и меняется только через [take] и [putBack], поэтому соседи под пальцем не переезжают.
 */
@Stable
class PackedPoolState<K : Any> internal constructor(
    internal val keys: List<K>,
    internal val columns: Int,
    private val seed: Int,
) {
    // Сетка после первого изменения пула; null — раскладка ещё за измерением.
    private var edited by mutableStateOf<List<K?>?>(null)

    // Последняя раскладка по высоте. Пишется при измерении, поэтому не snapshot-состояние.
    private var packed: List<K> = keys

    internal val isPacking: Boolean get() = edited == null

    internal val cells: List<K?> get() = edited ?: packed

    // Один и тот же seed даёт одну раскладку при повторных измерениях — строки не перетасовываются.
    internal fun pack(visible: List<K>, heightOf: (K) -> Int): List<K> =
        packPoolRows(visible, columns, heightOf, Random(seed)).also { packed = it }

    /** Элемент ушёл из пула: на его месте остаётся дыра. */
    fun take(key: K) {
        val next = cells.toMutableList()
        val index = next.indexOf(key)
        if (index < 0) return
        next[index] = null
        edited = next
    }

    /** Элемент вернулся в пул: в первую дыру строки, где ещё есть чипы, иначе — в конец. */
    fun putBack(key: K) {
        val next = cells.toMutableList()
        if (key in next) return
        val hole = next.indices.firstOrNull { i ->
            next[i] == null && rowOf(next, i).any { it != null }
        }
        if (hole != null) next[hole] = key else next += key
        edited = next
    }

    private fun rowOf(cells: List<K?>, index: Int): List<K?> {
        val start = index / columns * columns
        return cells.subList(start, minOf(start + columns, cells.size))
    }
}

/** Состояние пула для набора [keys] (в исходном перемешанном порядке) по [columns] ячеек в строке. */
@Composable
fun <K : Any> rememberPackedPoolState(keys: List<K>, columns: Int): PackedPoolState<K> =
    remember(keys, columns) { PackedPoolState(keys, columns, Random.nextInt()) }

/**
 * Сетка пула из слотов фиксированной ширины [slotWidth]; чип внутри слота по центру. Высота строки — по
 * самому высокому чипу, пустые строки места не занимают. Показываются только ключи, для которых
 * [isVisible] истинно. Пока пул не тронут, чипы расставляются по измеренной высоте ([PackedPoolState]).
 */
@Composable
fun <K : Any> PackedPool(
    state: PackedPoolState<K>,
    slotWidth: Dp,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    isVisible: (K) -> Boolean,
    modifier: Modifier = Modifier,
    item: @Composable (K) -> Unit,
) {
    val visible = state.keys.filter(isVisible)
    Layout(
        modifier = modifier,
        content = {
            visible.forEach { id ->
                key(id) {
                    Box(contentAlignment = Alignment.Center) { item(id) }
                }
            }
        },
    ) { measurables, constraints ->
        val slotPx = slotWidth.roundToPx()
        val hGapPx = horizontalSpacing.roundToPx()
        val vGapPx = verticalSpacing.roundToPx()
        val slotConstraints = Constraints.fixedWidth(slotPx)
        val placeables = visible.zip(measurables) { id, m -> id to m.measure(slotConstraints) }.toMap()

        val cells = if (state.isPacking) {
            state.pack(visible) { placeables.getValue(it).height }
        } else {
            state.cells.map { id -> id?.takeIf(placeables::containsKey) }
        }
        val rows = cells.chunked(state.columns).filter { row -> row.any { it != null } }
        val rowHeights = rows.map { row -> row.maxOf { id -> id?.let { placeables.getValue(it).height } ?: 0 } }

        val width = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            state.columns * slotPx + hGapPx * (state.columns - 1)
        }
        val height = rowHeights.sum() + vGapPx * (rows.size - 1).coerceAtLeast(0)

        layout(width, height) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { column, id ->
                    id?.let { placeables.getValue(it).place(column * (slotPx + hGapPx), y) }
                }
                y += rowHeights[rowIndex] + vGapPx
            }
        }
    }
}