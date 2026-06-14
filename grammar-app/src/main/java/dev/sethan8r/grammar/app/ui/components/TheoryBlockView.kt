package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.domain.model.CalloutVariant
import dev.sethan8r.grammar.app.domain.model.TheoryBlock
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Рендер списка блоков теории одной карточки. Каждый блок строится своим Composable по типу
 * (paragraph/heading/list/table/callout/divider). Контентный текст идёт через [MarkdownText]
 * (инлайн `**…**`/`*…*` + иконки ✓/✗); структурные маркеры (буллеты, номера) — обычный [Text].
 *
 * Разделение блоков: рисуем явные [TheoryBlock.Divider] (из `---` автора) и дополнительно тонкую
 * линию перед каждым [TheoryBlock.Heading] (если предыдущий блок не был линией) — чтобы границы
 * подсекций («Разница» и т.п.) читались, даже где автор `---` не поставил.
 */
@Composable
fun TheoryBlocks(blocks: List<TheoryBlock>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
        blocks.forEachIndexed { index, block ->
            val isEdge = index == 0 || index == blocks.lastIndex
            when (block) {
                is TheoryBlock.Divider -> if (!isEdge) ThinDivider()

                is TheoryBlock.Heading -> {
                    if (index > 0 && blocks[index - 1] !is TheoryBlock.Divider) ThinDivider()
                    MarkdownText(
                        text = block.text,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }

                is TheoryBlock.Paragraph -> MarkdownText(block.text)
                is TheoryBlock.BulletList -> ListBlock(block)
                is TheoryBlock.Table -> TableBlock(block)
                is TheoryBlock.Callout -> CalloutBlock(block)
            }
        }
    }
}

@Composable
private fun ThinDivider() {
    HorizontalDivider(color = Inactive)
}

@Composable
private fun ListBlock(block: TheoryBlock.BulletList) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        block.items.forEachIndexed { index, item ->
            Row {
                Text(
                    text = if (block.ordered) "${index + 1}.  " else "•  ",
                    color = Accent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                MarkdownText(item, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TableBlock(block: TheoryBlock.Table) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        if (block.header.isNotEmpty()) {
            TableRow(cells = block.header, isHeader = true)
            HorizontalDivider(color = Inactive)
        }
        block.rows.forEachIndexed { index, row ->
            TableRow(cells = row, isHeader = false)
            if (index < block.rows.lastIndex) HorizontalDivider(color = Inactive)
        }
    }
}

@Composable
private fun TableRow(cells: List<String>, isHeader: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        cells.forEach { cell ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
            ) {
                MarkdownText(
                    text = cell,
                    color = if (isHeader) Accent else TextPrimary,
                    fontWeight = if (isHeader) FontWeight.Bold else null,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun CalloutBlock(block: TheoryBlock.Callout) {
    val accentColor = when (block.variant) {
        CalloutVariant.TRAP -> IncorrectRed
        CalloutVariant.WARNING -> Accent
        CalloutVariant.FORMULA -> Accent
        CalloutVariant.TIP -> CorrectGreen
        CalloutVariant.NOTE -> TextSecondary
    }
    val monospace = if (block.variant == CalloutVariant.FORMULA) FontFamily.Monospace else null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        Box(
            modifier = Modifier
                .width(Dimens.calloutAccentBar)
                .fillMaxHeight()
                .background(accentColor),
        )
        Column(
            modifier = Modifier.weight(1f).padding(Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        ) {
            if (block.label.isNotBlank()) {
                MarkdownText(
                    text = block.label,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
            // Тело плашки — вложенные блоки (абзацы/список/таблица).
            block.blocks.forEach { CalloutBodyBlock(it, monospace) }
        }
    }
}

/** Рендер одного блока внутри плашки (тело callout не содержит divider/heading). */
@Composable
private fun CalloutBodyBlock(block: TheoryBlock, monospace: FontFamily?) {
    when (block) {
        is TheoryBlock.Paragraph -> MarkdownText(block.text, fontFamily = monospace)
        is TheoryBlock.BulletList -> ListBlock(block)
        is TheoryBlock.Table -> TableBlock(block)
        is TheoryBlock.Heading -> MarkdownText(
            text = block.text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        is TheoryBlock.Callout -> CalloutBlock(block)
        TheoryBlock.Divider -> Unit
    }
}