package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.components.text.parseInlineMarkdown
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/** Разделитель двойного имени «EN · RU». */
private const val DUAL_TITLE_SEPARATOR = " · "

/**
 * Английская часть двойного имени «EN · RU» (для шапок, где RU не нужен — карточки микротемы,
 * сессия упражнений). Если разделителя нет — возвращает строку как есть.
 */
fun String.titleEn(): String = substringBefore(DUAL_TITLE_SEPARATOR)

/**
 * Двойное название «EN · RU» (канон микротем): EN — основным цветом и жирным, RU — мельче и серым
 * под ним. Если ` · ` в строке нет — рендерит одну строку (тема ещё не приведена к формату).
 * Это интерфейсная подача названия из БД, поэтому обычный [Text], не TranslatableText; знаки-стрелки
 * в названии рисуются иконками через [parseInlineMarkdown] — как в тексте теории.
 */
@Composable
fun DualTitle(
    title: String,
    modifier: Modifier = Modifier,
    primarySize: TextUnit = 16.sp,
    primaryWeight: FontWeight = FontWeight.Bold,
) {
    val parts = title.split(" · ", limit = 2)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny)) {
        TitleLine(
            text = parts[0],
            color = TextPrimary,
            fontSize = primarySize,
            fontWeight = primaryWeight,
        )
        if (parts.size > 1) {
            TitleLine(text = parts[1], color = TextSecondary, fontSize = 13.sp)
        }
    }
}

/** Строка названия: символы-значки (`↔`, `→`) заменяются иконками, цвет иконки — цвет строки. */
@Composable
private fun TitleLine(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight? = null,
) {
    val parsed = parseInlineMarkdown(
        raw = text,
        correctColor = CorrectGreen,
        incorrectColor = IncorrectRed,
        arrowColor = color,
    )
    Text(
        text = parsed.text,
        inlineContent = parsed.inlineContent,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}