package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseChip
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import dev.sethan8r.grammar.app.ui.components.exercise.parts.detectChipDrag
import dev.sethan8r.grammar.app.ui.components.exercise.parts.hitTest

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.util.animatePlacement
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/** Слот: стабильный id (ключ анимации/хит-тест) + слово. */
private data class WordSlot(val id: Int, val token: String)

/**
 * Рендерер WORD_ARRANGEMENT во [ExerciseFrame]. Поле сборки (сверху) + банк слов (снизу), drag-and-drop
 * между ними. Жест — на КОНТЕЙНЕРЕ (а не на чипе): по нажатию хит-тестим слово под пальцем и таскаем
 * его. Так чип может свободно мигрировать между пулом и строкой, не выпадая из композиции (иначе drag
 * рвётся). Оверлей перетаскиваемого чипа рисуется поверх всего (zIndex). Жест — общий [detectChipDrag]
 * (drag стартует на чипе по slop, тап добавляет/возвращает слово; на пустом месте — скролл страницы).
 *
 * Слово «в предложении» ⟺ отпущено НАД полем сборки; иначе — возвращается в пул (с анимацией).
 * Источник правды отрисовки — локальный [sentence]; в VM уходят тексты по порядку для проверки.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordArrangementExerciseView(
    exercise: Exercise.WordArrangement,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onArrangementChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase.isEditable
    val solved = phase == AnswerPhase.CORRECT
    val scope = rememberCoroutineScope()

    // Банк: слова + дистракторы, перемешан один раз (стабильные id).
    val bank = remember(exercise.id) {
        (exercise.words + exercise.distractors)
            .mapIndexed { i, t -> WordSlot(id = i, token = t) }
            .shuffled()
    }
    val sentence: SnapshotStateList<WordSlot> = remember(exercise.id) { mutableStateListOf() }

    // Геометрия в координатах контейнера-обёртки.
    var wrapperCoords by remember(exercise.id) { mutableStateOf<LayoutCoordinates?>(null) }
    var assemblyBounds by remember(exercise.id) { mutableStateOf(Rect.Zero) }
    val sentenceCenters = remember(exercise.id) { mutableStateMapOf<Int, Offset>() }
    val sentenceSizes = remember(exercise.id) { mutableStateMapOf<Int, IntSize>() }
    val poolCenters = remember(exercise.id) { mutableStateMapOf<Int, Offset>() }
    val poolSizes = remember(exercise.id) { mutableStateMapOf<Int, IntSize>() }

    // Состояние drag / возврата.
    var dragging by remember(exercise.id) { mutableStateOf<WordSlot?>(null) }
    var releasing by remember(exercise.id) { mutableStateOf<WordSlot?>(null) }
    var pointer by remember(exercise.id) { mutableStateOf(Offset.Zero) }
    var draggedSize by remember(exercise.id) { mutableStateOf(IntSize.Zero) }
    var overSentence by remember(exercise.id) { mutableStateOf(false) }
    val releaseAnim = remember(exercise.id) { Animatable(Offset.Zero, Offset.VectorConverter) }

    fun commit() = onArrangementChanged(sentence.map { it.token })

    fun slotAt(pos: Offset): WordSlot? {
        sentence.forEach { slot ->
            val c = sentenceCenters[slot.id]; val s = sentenceSizes[slot.id]
            if (c != null && s != null && hitTest(c, s, pos)) return slot
        }
        bank.forEach { slot ->
            if (slot in sentence) return@forEach
            val c = poolCenters[slot.id]; val s = poolSizes[slot.id]
            if (c != null && s != null && hitTest(c, s, pos)) return slot
        }
        return null
    }

    fun toggle(slot: WordSlot) {
        if (slot in sentence) sentence.remove(slot) else sentence.add(slot)
        commit()
    }

    fun startDrag(slot: WordSlot) {
        val inSentence = slot in sentence
        dragging = slot
        pointer = (if (inSentence) sentenceCenters[slot.id] else poolCenters[slot.id]) ?: Offset.Zero
        draggedSize = (if (inSentence) sentenceSizes[slot.id] else poolSizes[slot.id]) ?: IntSize.Zero
        overSentence = inSentence
    }

    fun onDragMove(delta: Offset) {
        val slot = dragging ?: return
        pointer += delta
        overSentence = assemblyBounds.contains(pointer)
        if (overSentence) {
            val others = sentence.filter { it != slot }
            val idx = computeInsert(pointer, others, sentenceCenters, sentenceSizes).coerceIn(0, others.size)
            val cur = sentence.indexOf(slot)
            if (cur != idx) {
                if (cur >= 0) sentence.removeAt(cur)
                sentence.add(idx.coerceIn(0, sentence.size), slot)
            }
        } else {
            sentence.remove(slot)
        }
    }

    fun endDrag() {
        val slot = dragging ?: return
        dragging = null
        commit()
        if (!overSentence) {
            // Отпустили не над полем — слово возвращается в пул на своё место с анимацией.
            val from = pointer
            val to = poolCenters[slot.id] ?: pointer
            releasing = slot
            scope.launch {
                releaseAnim.snapTo(from)
                releaseAnim.animateTo(to, tween(durationMillis = Durations.dragReturnMs))
                releasing = null
            }
        }
    }

    // Внешний Box — жест-хост + начало координат геометрии + оверлей. Оверлей лежит СИБЛИНГОМ
    // ExerciseFrame (а не внутри него), поэтому НЕ обрезается клипом фрейма: перетаскиваемое слово
    // остаётся поверх фона даже когда уезжает за край фрейма.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { wrapperCoords = it }
            // Единый жест чипа, дружащий со скроллом: drag стартует только на чипе (slop), тап —
            // добавляет/возвращает слово; на пустом месте фрейма жест уходит родительскому скроллу.
            .pointerInput(exercise.id, editable) {
                if (!editable) return@pointerInput
                detectChipDrag(
                    hitTest = { slotAt(it) },
                    onStart = { startDrag(it) },
                    onDrag = { delta -> onDragMove(delta) },
                    onEnd = { endDrag() },
                    onTap = { toggle(it) },
                )
            },
    ) {
        ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey) {
            // Шапка-условие.
            Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
                Text(text = exercise.situationRu, color = TextPrimary, fontSize = 16.sp)
            }
            ExerciseDivider()

            Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
                // Цвет поля сборки по результату: верно — зелёное, 2-я ошибка (reveal) — красное.
                val fieldBorder = when {
                    solved -> CorrectGreen
                    phase == AnswerPhase.REVEALED -> IncorrectRed
                    else -> Inactive
                }
                val fieldFill = when {
                    solved -> CorrectGreen.copy(alpha = Alphas.answerFill)
                    phase == AnswerPhase.REVEALED -> IncorrectRed.copy(alpha = Alphas.answerFill)
                    else -> Color.Transparent
                }
                // --- Поле сборки (минимум ~2 строки, дальше растёт) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .onGloballyPositioned { ac ->
                            val w = wrapperCoords ?: return@onGloballyPositioned
                            val topLeft = w.localPositionOf(ac, Offset.Zero)
                            assemblyBounds = Rect(topLeft, Size(ac.size.width.toFloat(), ac.size.height.toFloat()))
                        }
                        .clip(RoundedCornerShape(Dimens.cornerButton))
                        .background(fieldFill)
                        .border(1.dp, fieldBorder, RoundedCornerShape(Dimens.cornerButton))
                        .padding(Dimens.spaceSmall),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                    ) {
                        sentence.forEach { slot ->
                            val isDragged = slot == dragging
                            // Слова — обычные; результат показывает РАМКА ПОЛЯ (выше). Перетаскиваемое
                            // место — пустой слот-индикатор (рамка размером со слово, текст скрыт).
                            key(slot.id) {
                                ExerciseChip(
                                    text = slot.token,
                                    // Когда поле окрашено результатом (верно — зелёное / 2-я ошибка —
                                    // красное), фон чипа прозрачный: заливка ПОЛЯ просвечивает сквозь
                                    // карточки (буквы остаются белыми). Тест — можно откатить.
                                    background = when {
                                        isDragged -> Background
                                        solved || phase == AnswerPhase.REVEALED -> Color.Transparent
                                        else -> Elevated
                                    },
                                    border = Inactive,
                                    contentAlpha = if (isDragged) 0f else 1f,
                                    modifier = Modifier
                                        .animatePlacement()
                                        .onGloballyPositioned { coords ->
                                            val w = wrapperCoords ?: return@onGloballyPositioned
                                            val s = coords.size
                                            sentenceSizes[slot.id] = s
                                            sentenceCenters[slot.id] =
                                                w.localPositionOf(coords, Offset(s.width / 2f, s.height / 2f))
                                        },
                                )
                            }
                        }
                    }
                }

                // Увеличенный зазор между полем сборки и пулом.
                Spacer(Modifier.height(Dimens.spaceXXLarge))

                // --- Банк слов (использованные/перетаскиваемые — тёмный плейсхолдер той же ширины) ---
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                ) {
                    bank.forEach { slot ->
                        val hidden = slot in sentence || slot == dragging || slot == releasing
                        // Спрятанный слот — полностью прозрачный (без тёмного фрейма), но держит место
                        // в раскладке (банк не сжимается): ширина чипа сохраняется, текст уже alpha 0.
                        ExerciseChip(
                            text = slot.token,
                            background = if (hidden) Color.Transparent else Elevated,
                            border = if (hidden) Color.Transparent else Inactive,
                            contentAlpha = if (hidden) 0f else 1f,
                            modifier = Modifier.onGloballyPositioned { coords ->
                                val w = wrapperCoords ?: return@onGloballyPositioned
                                val s = coords.size
                                poolSizes[slot.id] = s
                                poolCenters[slot.id] =
                                    w.localPositionOf(coords, Offset(s.width / 2f, s.height / 2f))
                            },
                        )
                    }
                }
            }

            // Правильное предложение — на реванше (слитые попытки). Верхний отступ = нижнему (до
            // разделителя объяснения, у него тоже cardPadding) — текст «Правильно» по центру зазора.
            if (phase == AnswerPhase.REVEALED) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = Dimens.cardPadding)
                        .padding(top = Dimens.cardPadding),
                ) {
                    Text(
                        text = stringResource(R.string.exercise_correct_answer, exercise.correctSentence),
                        color = CorrectGreen,
                        fontSize = 16.sp,
                    )
                }
            }

            ExerciseExplanation(phase = phase, text = exercise.explanation)
        }

        // Оверлей: перетаскиваемый/возвращаемый чип ПОВЕРХ фрейма (вне его клипа), без цветовой подсветки.
        val shown = dragging ?: releasing
        shown?.let { slot ->
            val pos = if (dragging != null) pointer else releaseAnim.value
            ExerciseChip(
                text = slot.token,
                background = Elevated,
                border = Inactive,
                modifier = Modifier
                    .zIndex(1f)
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

/** Индекс вставки в поле сборки по позиции пальца: число чипов, стоящих «до» точки (по строкам). */
private fun computeInsert(
    pointer: Offset,
    slots: List<WordSlot>,
    centers: Map<Int, Offset>,
    sizes: Map<Int, IntSize>,
): Int {
    var index = 0
    for (slot in slots) {
        val c = centers[slot.id] ?: continue
        val rowTol = (sizes[slot.id]?.height ?: 0) * 0.6f
        val before = c.y < pointer.y - rowTol || (abs(c.y - pointer.y) <= rowTol && c.x < pointer.x)
        if (before) index++
    }
    return index
}