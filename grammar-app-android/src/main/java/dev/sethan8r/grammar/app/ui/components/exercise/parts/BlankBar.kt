package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Полоска-пропуск: место под пропущенный ответ/реплику (вместо символов `____`). Блочный аналог
 * inline-пропуска из [dev.sethan8r.grammar.app.ui.components.text.parseInlineMarkdown] — тот живёт ВНУТРИ
 * `Text` (`InlineTextContent`, em-размер), а этот стоит самостоятельным элементом строки/слота.
 * Переиспользуемый (Правило №0): длина фиксирована ([width], как `____` — чтобы пропуск одинаково
 * выглядел и в середине предложения, и отдельной репликой); толщину/скругление/цвет даёт компонент.
 */
@Composable
fun BlankBar(
    modifier: Modifier = Modifier,
    width: Dp = Dimens.blankLineWidth,
    color: Color = TextSecondary,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(Dimens.blankLineThickness)
            .clip(RoundedCornerShape(percent = 50))
            .background(color),
    )
}