package dev.sethan8r.grammar.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextPrimary

/**
 * Единая точка рендера контентного текста с инлайн-разметкой [parseInlineMarkdown]
 * (`**жирный**`/`*курсив*` + иконки-вердикты ✓/✗) поверх [TranslatableText]. Правило №0: один
 * вызов на всю теорию, примеры и (позже) упражнения — чтобы `**…**` везде становился жирным.
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    fontSize: TextUnit = 16.sp,
    lineHeight: TextUnit = 24.sp,
    textAlign: TextAlign? = null,
) {
    val parsed = parseInlineMarkdown(
        raw = text,
        correctColor = CorrectGreen,
        incorrectColor = IncorrectRed,
        arrowColor = color,
    )
    TranslatableText(
        text = parsed.text,
        modifier = modifier,
        inlineContent = parsed.inlineContent,
        color = color,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        textAlign = textAlign,
    )
}