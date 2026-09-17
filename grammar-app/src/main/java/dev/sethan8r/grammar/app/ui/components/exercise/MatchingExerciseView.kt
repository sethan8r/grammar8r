package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.sethan8r.grammar.core.model.exercise.Exercise
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseContentText
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import dev.sethan8r.grammar.app.ui.components.exercise.parts.detectChipDrag
import dev.sethan8r.grammar.app.ui.components.exercise.parts.hitTest
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.animatePlacement
import kotlin.math.roundToInt

/** Слот правой колонки: стабильный id (ключ анимации/хит-тест) + текст. */
private data class RightSlot(val id: Int, val text: String)

/**
 * Рендерер MATCHING во [ExerciseFrame] — БЕЗ ниточек. Две колонки: левая закреплена (порядок
 * `pairs`), правую пользователь переставляет вертикальным drag-reorder (та же механика, что
 * WORD_ARRANGEMENT, но по одной вертикальной оси: фрейм под пальцем, остальные разъезжаются через
 * [animatePlacement]). Жест — на КОНТЕЙНЕРЕ (хит-тест слота под пальцем), оверлей — сиблингом фрейма
 * (поверх клипа). Строки фикс. высоты ([Dimens.matchRowHeight]) — левая и правая колонки совпадают по
 * индексу. Вердикт all-or-nothing: на реванше правая колонка доезжает в правильный порядок, при этом
 * изначально верно стоявшие — зелёные, ошибочные — красные (видно и правильный ответ, и где была ошибка).
 */
@Composable
fun MatchingExerciseView(
    exercise: Exercise.Matching,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onOrderChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase.isEditable
    val solved = phase == AnswerPhase.CORRECT
    val revealed = phase == AnswerPhase.REVEALED

    val correctRights = remember(exercise.id) { exercise.pairs.map { it.right } }
    // Правая колонка: тексты с стабильными id, перемешаны один раз (по возможности не как эталон).
    val order: SnapshotStateList<RightSlot> = remember(exercise.id) {
        correctRights.mapIndexed { i, text -> RightSlot(id = i, text = text) }
            .shuffledDeranged(correctRights)
            .toMutableStateList()
    }

    // Геометрия правых ячеек в координатах внешней обёртки (хит-тест/таргет вставки).
    var wrapperCoords by remember(exercise.id) { mutableStateOf<LayoutCoordinates?>(null) }
    val centers = remember(exercise.id) { mutableStateMapOf<Int, Offset>() }
    val sizes = remember(exercise.id) { mutableStateMapOf<Int, IntSize>() }

    var dragging by remember(exercise.id) { mutableStateOf<RightSlot?>(null) }
    var pointer by remember(exercise.id) { mutableStateOf(Offset.Zero) }
    var draggedSize by remember(exercise.id) { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    // Цвета реванша: какие слоты пользователь поставил верно (до доводки до правильного порядка).
    var revealCorrect by remember(exercise.id) { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }

    // Стартовую раскладку отправляем во VM один раз при монтировании упражнения.
    LaunchedEffect(exercise.id) { onOrderChanged(order.map { it.text }) }

    // Реванш: зафиксировать «кто стоял верно», затем доехать в правильный порядок (показ ответа).
    LaunchedEffect(revealed) {
        if (!revealed) return@LaunchedEffect
        revealCorrect = order.associate { slot ->
            slot.id to (order.indexOf(slot).let { pos -> correctRights.getOrNull(pos) == slot.text })
        }
        val correctOrder = correctRights.map { right -> order.first { it.text == right } }
        order.clear()
        order.addAll(correctOrder)
    }

    fun commit() = onOrderChanged(order.map { it.text })

    fun slotAt(pos: Offset): RightSlot? = order.firstOrNull { slot ->
        val c = centers[slot.id]; val s = sizes[slot.id]
        c != null && s != null && hitTest(c, s, pos)
    }

    fun onDragMove(delta: Offset) {
        val slot = dragging ?: return
        pointer += delta
        // Целевой индекс по вертикали: сколько ДРУГИХ слотов выше точки пальца.
        val target = order.filter { it != slot }.count { (centers[it.id]?.y ?: Float.MAX_VALUE) < pointer.y }
        val cur = order.indexOf(slot)
        if (cur >= 0 && cur != target) {
            order.removeAt(cur)
            order.add(target.coerceIn(0, order.size), slot)
            commit()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { wrapperCoords = it }
            // Drag правых ячеек, дружащий со скроллом: стартует только на ячейке (см. detectChipDrag).
            .pointerInput(exercise.id, editable) {
                if (!editable) return@pointerInput
                detectChipDrag(
                    hitTest = { slotAt(it) },
                    onStart = { slot ->
                        dragging = slot
                        pointer = centers[slot.id] ?: Offset.Zero
                        draggedSize = sizes[slot.id] ?: IntSize.Zero
                    },
                    onDrag = { delta -> onDragMove(delta) },
                    onEnd = { dragging = null },
                )
            },
    ) {
        ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey) {
            // Шапка-условие (с заглавной буквы — задаём в коде, чтобы не править контент).
            Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
                Text(
                    text = exercise.taskDescription.replaceFirstChar { it.uppercase() },
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                )
            }

            ExerciseDivider()

            Row(
                modifier = Modifier.padding(horizontal = Dimens.cardPadding),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            ) {
                // Левая колонка — закреплена, в порядке pairs.
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                ) {
                    exercise.pairs.forEach { pair ->
                        MatchCell(
                            text = pair.left,
                            background = Background,
                            border = Inactive,
                            framed = false,
                            alignStart = true,
                        )
                    }
                }
                // Правая колонка — переставляемая.
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                ) {
                    order.forEach { slot ->
                        val isDragged = slot == dragging
                        val wasCorrect = revealCorrect[slot.id] == true
                        val border = when {
                            solved -> CorrectGreen
                            revealed && wasCorrect -> CorrectGreen
                            revealed -> IncorrectRed
                            else -> Inactive
                        }
                        val fill = when {
                            solved -> CorrectGreen.copy(alpha = Alphas.answerFill)
                            revealed && wasCorrect -> CorrectGreen.copy(alpha = Alphas.answerFill)
                            revealed -> IncorrectRed.copy(alpha = Alphas.answerFill)
                            isDragged -> Background
                            else -> Elevated
                        }
                        key(slot.id) {
                            MatchCell(
                                text = slot.text,
                                background = fill,
                                border = border,
                                dragHandle = true,
                                contentAlpha = if (isDragged) 0f else 1f,
                                modifier = Modifier
                                    .animatePlacement()
                                    .onGloballyPositioned { coords ->
                                        val w = wrapperCoords ?: return@onGloballyPositioned
                                        val s = coords.size
                                        sizes[slot.id] = s
                                        centers[slot.id] =
                                            w.localPositionOf(coords, Offset(s.width / 2f, s.height / 2f))
                                    },
                            )
                        }
                    }
                }
            }

            ExerciseExplanation(phase = phase, text = exercise.explanation)
        }

        // Оверлей перетаскиваемой правой ячейки — поверх фрейма (вне его клипа). Ширина = ширине
        // исходной ячейки (правая колонка weight 1f), центр — под пальцем.
        dragging?.let { slot ->
            MatchCell(
                text = slot.text,
                background = Elevated,
                border = Inactive,
                dragHandle = true,
                modifier = Modifier
                    .zIndex(1f)
                    .width(with(density) { draggedSize.width.toDp() })
                    .offset {
                        IntOffset(
                            (pointer.x - draggedSize.width / 2f).roundToInt(),
                            (pointer.y - draggedSize.height / 2f).roundToInt(),
                        )
                    },
            )
        }
    }
}

/**
 * Ячейка matching фикс. высоты, контентный EN/RU ([ExerciseContentText] — рендерит `**жирный**`).
 * - [framed] = `false` (левый столбец): без фона/рамки, текст к левому краю ([alignStart]).
 * - [dragHandle] = `true` (правый, переставляемый): 2 точки в углу — намёк «перетаскивается».
 */
@Composable
private fun MatchCell(
    text: String,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier,
    contentAlpha: Float = 1f,
    framed: Boolean = true,
    alignStart: Boolean = false,
    dragHandle: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.matchRowHeight)
            .then(
                if (framed) {
                    Modifier
                        .clip(RoundedCornerShape(Dimens.cornerButton))
                        .background(background)
                        .border(1.dp, border, RoundedCornerShape(Dimens.cornerButton))
                } else {
                    Modifier
                }
            ),
        contentAlignment = if (alignStart) Alignment.CenterStart else Alignment.Center,
    ) {
        // Горизонтальный отступ — на самом тексте (не на Box), чтобы точки-хэндл отмерялись от рамки.
        ExerciseContentText(
            text = text,
            color = TextPrimary,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(horizontal = if (framed) Dimens.spaceMedium else 0.dp)
                .alpha(contentAlpha),
        )
        if (dragHandle) {
            DragHandleDots(
                color = border,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.spaceSmall)
                    .alpha(contentAlpha),
            )
        }
    }
}

/** Две точки-«ручка» цвета обводки ячейки — намёк, что правый элемент можно перетаскивать. */
@Composable
private fun DragHandleDots(color: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMicro)) {
        repeat(2) {
            Box(
                Modifier
                    .size(Dimens.matchDragHandleDot)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

/**
 * Дерандж-шафл: НИ ОДИН элемент не стартует на своём правильном месте [correct] — иначе часть
 * задания решена ещё до касания. Подбор перебором: при 4–6 парах доля деранджей ~37%, так что
 * 32 попыток хватает практически всегда (вероятность фолбэка ~4e-7).
 */
private fun List<RightSlot>.shuffledDeranged(correct: List<String>): List<RightSlot> {
    if (size < 2) return this
    repeat(32) {
        val s = shuffled()
        if (s.indices.all { s[it].text != correct[it] }) return s
    }
    return shuffled()
}