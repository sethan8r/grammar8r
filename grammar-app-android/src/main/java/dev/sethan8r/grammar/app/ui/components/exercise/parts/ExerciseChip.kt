package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import kotlin.math.abs

/**
 * Перетаскиваемый чип контентного EN-текста ([ExerciseContentText]) — общий «кирпич» drag-заданий
 * (WORD_ARRANGEMENT / CATEGORIZATION / MATCHING). Фон/рамку/прозрачность задаёт вызывающий (состояние
 * выбора/результата). Текст всегда занимает место (alpha 0 у плейсхолдера) — раскладка не прыгает.
 */
@Composable
fun ExerciseChip(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    border: Color = Inactive,
    contentAlpha: Float = 1f,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.cornerButton))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(Dimens.cornerButton))
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
        contentAlignment = Alignment.Center,
    ) {
        ExerciseContentText(
            text = text,
            color = TextPrimary,
            fontSize = 16.sp,
            modifier = Modifier.alpha(contentAlpha),
        )
    }
}

/** Лежит ли точка [p] внутри прямоугольника с центром [c] и размером [s] (хит-тест чипа под пальцем). */
fun hitTest(c: Offset, s: IntSize, p: Offset): Boolean =
    abs(p.x - c.x) <= s.width / 2f && abs(p.y - c.y) <= s.height / 2f

/**
 * Жест перетаскивания чипа, СОВМЕСТИМЫЙ с вертикальным скроллом страницы (WORD_ARRANGEMENT /
 * CATEGORIZATION / MATCHING): drag начинается ТОЛЬКО если палец опустился на чип ([hitTest] != null) и
 * пересёк touch-slop; на пустом месте фрейма жест НЕ перехватывается — его получает родительский
 * `verticalScroll`. Так перетаскивание чипов и прокрутка длинного задания не конфликтуют (в отличие от
 * голого `detectDragGestures`, который глотает slop везде и блокирует скролл).
 *
 * Если палец опущен на чип и отпущен ДО slop — это тап ([onTap], опционально: WORD_ARRANGEMENT
 * быстро добавляет/возвращает слово). [onEnd] вызывается по завершении/отмене drag — финализирует
 * сброс (положить куда отпущено / вернуть в пул).
 */
suspend fun <T> PointerInputScope.detectChipDrag(
    hitTest: (Offset) -> T?,
    onStart: (item: T) -> Unit,
    onDrag: (delta: Offset) -> Unit,
    onEnd: () -> Unit,
    onTap: ((item: T) -> Unit)? = null,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val item = hitTest(down.position) ?: return@awaitEachGesture // не на чипе → жест уходит скроллу
        val slop = awaitTouchSlopOrCancellation(down.id) { change, _ -> change.consume() }
        if (slop == null) {
            // Отпущено до slop — короткий тап по чипу (не движение и не скролл).
            onTap?.invoke(item)
            return@awaitEachGesture
        }
        onStart(item)
        drag(slop.id) { change ->
            onDrag(change.positionChange())
            change.consume()
        }
        onEnd()
    }
}