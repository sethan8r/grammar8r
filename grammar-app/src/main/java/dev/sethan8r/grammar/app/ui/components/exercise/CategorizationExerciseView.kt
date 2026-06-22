package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.util.animatePlacement
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.roundToInt

/** Элемент категоризации: стабильный id, текст и индекс правильной колонки. */
private data class CatItem(val id: Int, val text: String, val correctCol: Int)

/**
 * Рендерер CATEGORIZATION во [ExerciseFrame]: колонки-категории сверху, пул снизу, drag элементов
 * пул ↔ колонки (туда-обратно). Жест — на КОНТЕЙНЕРЕ (хит-тест элемента под пальцем), оверлей —
 * сиблингом фрейма (поверх клипа); зона сброса определяется по координатам колонок/пула. Элементы
 * пула пресайзятся под ширину колонки ([BoxWithConstraints]) — при броске нет скачка размера. Вердикт
 * all-or-nothing: на реванше каждый элемент уезжает в свою колонку (показ ответа), при этом изначально
 * верно лежавшие — зелёные, ошибочные — красные (видно и правильный ответ, и где была ошибка).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorizationExerciseView(
    exercise: Exercise.Categorization,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onPlacementChanged: (Map<String, Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase.isEditable
    val solved = phase == AnswerPhase.CORRECT
    val revealed = phase == AnswerPhase.REVEALED
    val density = LocalDensity.current

    // Плоский список элементов (стабильные id), перемешан для пула один раз.
    val items = remember(exercise.id) {
        exercise.categories.flatMapIndexed { col, category ->
            category.items.map { text -> text to col }
        }.mapIndexed { i, (text, col) -> CatItem(id = i, text = text, correctCol = col) }
            .shuffled()
    }
    val textById = remember(exercise.id) { items.associate { it.id to it.text } }

    // Раскладка: id элемента → индекс колонки. Отсутствует в карте ⟺ элемент в пуле.
    val placement = remember(exercise.id) { mutableStateMapOf<Int, Int>() }

    // Геометрия в координатах внешней обёртки.
    var wrapperCoords by remember(exercise.id) { mutableStateOf<LayoutCoordinates?>(null) }
    val zoneBounds = remember(exercise.id) { mutableStateMapOf<Int, Rect>() } // ключ: индекс колонки (пул — не зона)
    val centers = remember(exercise.id) { mutableStateMapOf<Int, Offset>() }
    val sizes = remember(exercise.id) { mutableStateMapOf<Int, IntSize>() }

    var dragging by remember(exercise.id) { mutableStateOf<CatItem?>(null) }
    var pointer by remember(exercise.id) { mutableStateOf(Offset.Zero) }
    var draggedSize by remember(exercise.id) { mutableStateOf(IntSize.Zero) }
    var hoverZone by remember(exercise.id) { mutableStateOf<Int?>(null) }
    // Плавный «переезд» оверлея к новому месту чипа после сброса (колонка/пул — разные родители, поэтому
    // animatePlacement тут не срабатывает; анимируем оверлей, как возврат в пул в WORD_ARRANGEMENT).
    var releasing by remember(exercise.id) { mutableStateOf<CatItem?>(null) }
    val releaseAnim = remember(exercise.id) { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scope = rememberCoroutineScope()

    // Элементы, которые на момент проверки стояли НЕ в своей колонке (для подсветки на реванше).
    var revealWrong by remember(exercise.id) { mutableStateOf<Set<Int>>(emptySet()) }

    fun commit() = onPlacementChanged(placement.entries.associate { (id, col) -> textById.getValue(id) to col })

    // Реванш: зафиксировать ошибки, затем разложить всё по правильным колонкам (показ ответа).
    LaunchedEffect(revealed) {
        if (!revealed) return@LaunchedEffect
        revealWrong = items.filter { placement[it.id] != it.correctCol }.map { it.id }.toSet()
        items.forEach { placement[it.id] = it.correctCol }
    }

    fun zoneAt(pos: Offset): Int? = zoneBounds.entries.firstOrNull { it.value.contains(pos) }?.key

    fun itemAt(pos: Offset): CatItem? = items.firstOrNull { item ->
        val c = centers[item.id]; val s = sizes[item.id]
        c != null && s != null && hitTest(c, s, pos)
    }

    fun chipVisual(item: CatItem): Pair<Color, Color> {
        val wasWrong = item.id in revealWrong
        val border = when {
            solved -> CorrectGreen
            revealed && wasWrong -> IncorrectRed
            revealed -> CorrectGreen
            else -> Inactive
        }
        val fill = when {
            solved -> CorrectGreen.copy(alpha = Alphas.answerFill)
            revealed && wasWrong -> IncorrectRed.copy(alpha = Alphas.answerFill)
            revealed -> CorrectGreen.copy(alpha = Alphas.answerFill)
            // Перетаскиваемый/«переезжающий» — пустой плейсхолдер на своём месте (его рисует оверлей).
            item == dragging || item == releasing -> Background
            else -> Elevated
        }
        return border to fill
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { wrapperCoords = it }
            // Drag элементов, дружащий со скроллом: стартует только на чипе (см. detectChipDrag); на
            // пустом месте фрейма жест уходит родительскому скроллу.
            .pointerInput(exercise.id, editable) {
                if (!editable) return@pointerInput
                detectChipDrag(
                    hitTest = { itemAt(it) },
                    onStart = { item ->
                        dragging = item
                        pointer = centers[item.id] ?: Offset.Zero
                        draggedSize = sizes[item.id] ?: IntSize.Zero
                    },
                    onDrag = { delta ->
                        pointer += delta
                        hoverZone = zoneAt(pointer)
                    },
                    onEnd = {
                        val item = dragging
                        if (item != null) {
                            // Брошено на колонку → в неё; иначе (мимо колонок) → возврат в пул (как WORD_ARRANGEMENT).
                            val fromCol = placement[item.id]
                            val toCol = zoneAt(pointer)
                            val preCenter = centers[item.id] ?: pointer
                            if (toCol != null) placement[item.id] = toCol else placement.remove(item.id)
                            commit()
                            // Плавно «довозим» оверлей до места, где чип осядет. Сменилась зона → ждём новый
                            // центр после перераскладки (snapshotFlow); та же зона → центр прежний (preCenter).
                            releasing = item
                            val from = pointer
                            val zoneChanged = toCol != fromCol
                            scope.launch {
                                releaseAnim.snapTo(from)
                                val target = if (zoneChanged) {
                                    withTimeoutOrNull(Durations.dragReturnMs.toLong() * 2) {
                                        snapshotFlow { centers[item.id] }.first { it != null && it != preCenter }
                                    } ?: centers[item.id] ?: from
                                } else {
                                    preCenter
                                }
                                releaseAnim.animateTo(target, tween(durationMillis = Durations.dragReturnMs))
                                releasing = null
                            }
                        }
                        dragging = null
                        hoverZone = null
                    },
                )
            },
    ) {
        ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey) {
            Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
                Text(
                    text = exercise.taskDescription.replaceFirstChar { it.uppercase() },
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                )
            }

            ExerciseDivider()

            Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
                BoxWithConstraints {
                    val n = exercise.categories.size
                    // Ширина чипа = ширина внутренней области колонки (общая для колонок и пула — пресайз).
                    val columnWidth = (maxWidth - Dimens.spaceSmall * (n - 1)) / n
                    val chipWidth = columnWidth - Dimens.spaceSmall * 2

                    Column {
                        // --- Колонки-категории ---
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                            exercise.categories.forEachIndexed { col, category ->
                                CategoryColumn(
                                    title = category.title,
                                    width = columnWidth,
                                    highlighted = hoverZone == col,
                                    onBoundsChanged = { rect -> zoneBounds[col] = rect },
                                    wrapperCoords = wrapperCoords,
                                ) {
                                    items.filter { placement[it.id] == col }.forEach { item ->
                                        val (border, fill) = chipVisual(item)
                                        key(item.id) {
                                            CatChip(
                                                text = item.text,
                                                width = chipWidth,
                                                background = fill,
                                                border = border,
                                                contentAlpha = if (item == dragging || item == releasing) 0f else 1f,
                                                onGeometry = { c, s -> centers[item.id] = c; sizes[item.id] = s },
                                                wrapperCoords = wrapperCoords,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(Dimens.spaceXLarge))

                        // --- Пул (нераспределённые) — без рамки (как банк в WORD_ARRANGEMENT). Брошенный
                        // мимо колонок элемент возвращается сюда, поэтому пул как «зона» не регистрируется.
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                        ) {
                            items.filter { placement[it.id] == null }.forEach { item ->
                                val (border, fill) = chipVisual(item)
                                key(item.id) {
                                    CatChip(
                                        text = item.text,
                                        width = chipWidth,
                                        background = fill,
                                        border = border,
                                        contentAlpha = if (item == dragging || item == releasing) 0f else 1f,
                                        onGeometry = { c, s -> centers[item.id] = c; sizes[item.id] = s },
                                        wrapperCoords = wrapperCoords,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            ExerciseExplanation(phase = phase, text = exercise.explanation)
        }

        // Оверлей перетаскиваемого/«переезжающего» элемента — поверх фрейма (вне его клипа). Под пальцем
        // при drag; после сброса плавно едет к новому месту чипа (releaseAnim). Прямой ExerciseChip
        // (без animatePlacement/захвата геометрии — позицию держит offset).
        (dragging ?: releasing)?.let { item ->
            val pos = if (dragging != null) pointer else releaseAnim.value
            ExerciseChip(
                text = item.text,
                background = Elevated,
                border = Inactive,
                modifier = Modifier
                    .zIndex(1f)
                    .width(with(density) { draggedSize.width.toDp() })
                    .offset {
                        IntOffset(
                            (pos.x - draggedSize.width / 2f).roundToInt(),
                            (pos.y - draggedSize.height / 2f).roundToInt(),
                        )
                    },
            )
        }
    }
}

/** Колонка-категория: заголовок + зона сброса фикс. ширины; рамка-акцент при наведении перетаскиванием. */
@Composable
private fun CategoryColumn(
    title: String,
    width: Dp,
    highlighted: Boolean,
    onBoundsChanged: (Rect) -> Unit,
    wrapperCoords: LayoutCoordinates?,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(width)
            .heightIn(min = Dimens.categoryColumnMinHeight)
            .clip(RoundedCornerShape(Dimens.cornerButton))
            .border(
                width = 1.dp,
                color = if (highlighted) Accent else Inactive,
                shape = RoundedCornerShape(Dimens.cornerButton),
            )
            .onGloballyPositioned { ac ->
                val w = wrapperCoords ?: return@onGloballyPositioned
                val tl = w.localPositionOf(ac, Offset.Zero)
                onBoundsChanged(Rect(tl, Size(ac.size.width.toFloat(), ac.size.height.toFloat())))
            }
            .padding(Dimens.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            color = Accent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        content()
    }
}

/** Чип-элемент категоризации фикс. ширины [width] (пресайз под колонку). Контентный EN ([ExerciseChip]). */
@Composable
private fun CatChip(
    text: String,
    width: Dp,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier,
    contentAlpha: Float = 1f,
    onGeometry: ((Offset, IntSize) -> Unit)? = null,
    wrapperCoords: LayoutCoordinates? = null,
) {
    ExerciseChip(
        text = text,
        background = background,
        border = border,
        contentAlpha = contentAlpha,
        modifier = modifier
            .width(width)
            .animatePlacement()
            .onGloballyPositioned { coords ->
                val w = wrapperCoords ?: return@onGloballyPositioned
                val s = coords.size
                onGeometry?.invoke(
                    w.localPositionOf(coords, Offset(s.width / 2f, s.height / 2f)),
                    s,
                )
            },
    )
}