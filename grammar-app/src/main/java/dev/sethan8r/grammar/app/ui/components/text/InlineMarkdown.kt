package dev.sethan8r.grammar.app.ui.components.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.sethan8r.grammar.app.ui.theme.InlineCode

/** Результат разбора: текст + карта инлайн-иконок (вердикт ✓/✗) для [TranslatableText]. */
data class ParsedMarkdown(
    val text: AnnotatedString,
    val inlineContent: Map<String, InlineTextContent>,
)

private const val INLINE_CHECK = "inline_check"
private const val INLINE_CROSS = "inline_cross"
private const val INLINE_ARROW = "inline_arrow"
private const val INLINE_NEQ = "inline_neq"
private const val INLINE_APPROX = "inline_approx"
private const val INLINE_BLANK = "inline_blank"

/**
 * Единая утилита инлайн-разметки контента (правило №0 — её же переиспользует движок упражнений).
 * Разбирает текстовые маркеры (канон theory_content_guide §8):
 *  - `**жирный**`  → [FontWeight.Bold]
 *  - `*курсив*`    → [FontStyle.Italic] (так размечены переводы английских примеров)
 *  - `` `код` ``   → цвет [inlineCodeColor] (инлайн-вставки английского; бэктики в текст не идут)
 *
 * Символы-глифы в данных заменяются на **векторные иконки Material** через официальный
 * `InlineTextContent` (в текст эмодзи не попадают, размер — в `em`, тянется за шрифтом):
 *  - `✓` → [Icons.Filled.Check] (зелёный), `✗`/`❌` → [Icons.Filled.Close] (красный);
 *  - `→` → [Icons.AutoMirrored.Filled.ArrowRightAlt] (цветом текста [arrowColor]);
 *  - `≠` → [NotEqualIcon], `≈` → [ApproxEqualIcon] (цветом текста [arrowColor]) — в наборе
 *    Material таких значков нет, поэтому векторы нарисованы здесь.
 * Карту иконок отдаём в [TranslatableText] вместе с текстом.
 */
fun parseInlineMarkdown(
    raw: String,
    correctColor: Color,
    incorrectColor: Color,
    arrowColor: Color,
    inlineCodeColor: Color = InlineCode,
    renderBlanks: Boolean = false,
): ParsedMarkdown {
    val text = buildAnnotatedString {
        var index = 0
        var boldDepth = 0
        var italicDepth = 0
        var codeDepth = 0

        while (index < raw.length) {
            when {
                raw.startsWith("**", index) -> {
                    if (boldDepth == 0) pushStyle(SpanStyle(fontWeight = FontWeight.Bold)) else pop()
                    boldDepth = if (boldDepth == 0) 1 else 0
                    index += 2
                }

                raw[index] == '*' -> {
                    if (italicDepth == 0) pushStyle(SpanStyle(fontStyle = FontStyle.Italic)) else pop()
                    italicDepth = if (italicDepth == 0) 1 else 0
                    index += 1
                }

                // Бэктик-вставка `...` → цвет [inlineCodeColor] + Medium-вес + курсив (без них
                // выделение блёклое); сами бэктики в текст не попадают.
                raw[index] == '`' -> {
                    if (codeDepth == 0) {
                        pushStyle(
                            SpanStyle(
                                color = inlineCodeColor,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic,
                            ),
                        )
                    } else {
                        pop()
                    }
                    codeDepth = if (codeDepth == 0) 1 else 0
                    index += 1
                }

                // Пропуск в условии (`__`+) → сплошная инлайн-линия, а не символы подчёркивания.
                // Только в упражнениях ([renderBlanks]); в теории подчёркивания остаются текстом.
                renderBlanks && raw.startsWith("__", index) -> {
                    var end = index
                    while (end < raw.length && raw[end] == '_') end++
                    appendInlineContent(INLINE_BLANK, "___")
                    index = end
                }

                else -> {
                    when (raw[index]) {
                        '✓' -> appendInlineContent(INLINE_CHECK, "✓")
                        '✗', '❌' -> appendInlineContent(INLINE_CROSS, "✗")
                        '→' -> appendInlineContent(INLINE_ARROW, "→")
                        '≠' -> appendInlineContent(INLINE_NEQ, "≠")
                        '≈' -> appendInlineContent(INLINE_APPROX, "≈")
                        else -> append(raw[index])
                    }
                    index += 1
                }
            }
        }

        // Подстраховка от непарных маркеров в данных — закрываем открытые стили.
        repeat(boldDepth + italicDepth + codeDepth) { pop() }
    }

    val inlineContent = mapOf(
        INLINE_CHECK to inlineIcon(Icons.Filled.Check, correctColor),
        INLINE_CROSS to inlineIcon(Icons.Filled.Close, incorrectColor),
        INLINE_ARROW to inlineIcon(Icons.AutoMirrored.Filled.ArrowRightAlt, arrowColor),
        INLINE_NEQ to inlineIcon(NotEqualIcon, arrowColor),
        INLINE_APPROX to inlineIcon(ApproxEqualIcon, arrowColor),
        INLINE_BLANK to inlineBlank(arrowColor),
    )

    return ParsedMarkdown(text, inlineContent)
}

/** Пропуск в условии — сплошная линия по низу строки (вместо символов `___`). */
private fun inlineBlank(color: Color): InlineTextContent =
    InlineTextContent(
        placeholder = Placeholder(
            width = 2.6.em,
            height = 1.2.em,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 3.dp)
                    .height(2.dp)
                    .background(color),
            )
        }
    }

/**
 * Значок «не равно» (`≠`): две горизонтали равенства + косая черта. В наборе Material его нет,
 * поэтому рисуем вектором. Цвет штрихов неважен — [Icon] перекрашивает через `tint`.
 */
private val NotEqualIcon: ImageVector = ImageVector.Builder(
    name = "NotEqual",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    val stroke = SolidColor(Color.Black)
    val strokeWidth = 2.2f
    path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round) {
        moveTo(5f, 10f); lineTo(19f, 10f)
    }
    path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round) {
        moveTo(5f, 14f); lineTo(19f, 14f)
    }
    path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round) {
        moveTo(16f, 5f); lineTo(8f, 19f)
    }
}.build()

/**
 * Значок «приблизительно равно» (`≈`): две волнистые линии (тильды) друг над другом. В наборе
 * Material его нет, поэтому рисуем вектором. Цвет штрихов неважен — [Icon] перекрашивает через `tint`.
 */
private val ApproxEqualIcon: ImageVector = ImageVector.Builder(
    name = "ApproxEqual",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    val stroke = SolidColor(Color.Black)
    val strokeWidth = 2.2f
    path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round) {
        moveTo(5f, 11f); curveTo(7f, 8f, 10f, 8f, 12f, 10f); curveTo(14f, 12f, 17f, 12f, 19f, 9f)
    }
    path(stroke = stroke, strokeLineWidth = strokeWidth, strokeLineCap = StrokeCap.Round) {
        moveTo(5f, 15f); curveTo(7f, 12f, 10f, 12f, 12f, 14f); curveTo(14f, 16f, 17f, 16f, 19f, 13f)
    }
}.build()

/** Иконка размером с текущую строку текста (em-единицы), выровненная по центру строки. */
private fun inlineIcon(icon: ImageVector, tint: Color): InlineTextContent =
    InlineTextContent(
        placeholder = Placeholder(
            width = 1.2.em,
            height = 1.2.em,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.fillMaxSize(),
        )
    }