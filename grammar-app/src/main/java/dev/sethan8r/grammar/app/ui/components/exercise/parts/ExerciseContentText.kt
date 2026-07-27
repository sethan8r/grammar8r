package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.components.text.InlineArrowIcon
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.theme.TextPrimary

/**
 * Контентный текст ВНУТРИ задания. Отличается от [MarkdownText] ровно одним: пропуск `___` здесь
 * ВСЕГДА рисуется сплошной линией-пропуском (`renderBlanks = true`), а не остаётся символами `___`.
 *
 * Весь пакет `ui/components/exercise/` — только контент задания (теории тут нет), поэтому «рисовать
 * `___` как линию» — это инвариант пакета, а не опция конкретного экрана. Правило №0: инвариант
 * зафиксирован в ОДНОЙ точке — все рендеры заданий печатают контент через эту обёртку, и литеральный
 * `___` в задании становится физически невозможным (нельзя «забыть проставить флаг» на новом call-site).
 *
 * Сам [MarkdownText] сохраняет параметр `renderBlanks` для теории, где `___` — это обычный текст.
 */
@Composable
fun ExerciseContentText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    fontSize: TextUnit = 16.sp,
    lineHeight: TextUnit = 24.sp,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    arrowIcon: ImageVector = InlineArrowIcon,
    arrowIconColor: Color = color,
) = MarkdownText(
    text = text,
    modifier = modifier,
    color = color,
    fontWeight = fontWeight,
    fontFamily = fontFamily,
    fontSize = fontSize,
    lineHeight = lineHeight,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    renderBlanks = true,
    arrowIcon = arrowIcon,
    arrowIconColor = arrowIconColor,
)