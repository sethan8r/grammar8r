package dev.sethan8r.grammar.app.ui.components.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import dev.sethan8r.grammar.app.domain.model.theory.CalloutVariant
import dev.sethan8r.grammar.app.domain.model.theory.TheoryBlock
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Highlight
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

private val WHITESPACE = Regex("\\s+")

// Одиночное слово-ярлык длиннее — режем сбалансированно (≈ пополам), чтобы колонка под короткий
// контент не была широкой из-за длинного заголовка. Переноса по слогам в Compose нет → делим по символам.
private const val MAX_WORD_LEN = 9

@Composable
private fun TableBlock(block: TheoryBlock.Table) {
    val columnCount = maxOf(block.header.size, block.rows.maxOfOrNull { it.size } ?: 0)
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        val totalWidth = maxWidth
        // Готовим текст (перенос длинных слов) и ширины колонок один раз на (таблица + ширина).
        val table = remember(block, totalWidth) {
            val header = block.header.map(::wrapLongWords)
            val rows = block.rows.map { row -> row.map(::wrapLongWords) }
            val widths = columnWidths(header, rows, columnCount, totalWidth, measurer, density)
            Triple(header, rows, widths)
        }
        val (header, rows, widths) = table

        Column(modifier = Modifier.fillMaxWidth()) {
            if (header.isNotEmpty()) {
                TableRow(cells = header, isHeader = true, widths = widths)
                HorizontalDivider(color = Inactive)
            }
            rows.forEachIndexed { index, row ->
                TableRow(cells = row, isHeader = false, widths = widths)
                if (index < rows.lastIndex) HorizontalDivider(color = Inactive)
            }
        }
    }
}

/**
 * Делит ОДИНОЧНОЕ слово-ярлык длиннее [MAX_WORD_LEN] на сбалансированные части (через `\n`). В
 * многословных ячейках (предложениях) слова не трогаем — они переносятся по пробелам. `*` не трогаем.
 */
private fun wrapLongWords(cell: String): String {
    val trimmed = cell.trim()
    val isSingleWord = trimmed.isNotEmpty() && trimmed.none { it.isWhitespace() }
    if (!isSingleWord || trimmed.length <= MAX_WORD_LEN || trimmed.contains('*')) return cell
    val parts = ceil(trimmed.length / MAX_WORD_LEN.toDouble()).toInt()
    val size = ceil(trimmed.length / parts.toDouble()).toInt()
    return trimmed.chunked(size).joinToString("\n")
}

/**
 * Реальные ширины колонок: каждая ≥ ширины своего самого длинного слова (измеряем [TextMeasurer]
 * жирным — худший случай), поэтому слова НЕ рвутся по буквам. Остаток ширины раздаём пропорционально.
 */
private fun columnWidths(
    header: List<String>,
    rows: List<List<String>>,
    columnCount: Int,
    totalWidth: Dp,
    measurer: TextMeasurer,
    density: Density,
): List<Dp> {
    val style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
    val cellPadding = Dimens.spaceSmall * 2
    val minWidths = (0 until columnCount).map { column ->
        val longestToken = (listOf(header) + rows)
            .mapNotNull { it.getOrNull(column) }
            .flatMap { it.replace("*", "").split(WHITESPACE) }
            .maxByOrNull { it.length }
            .orEmpty()
        val tokenPx = measurer.measure(AnnotatedString(longestToken), style).size.width
        with(density) { tokenPx.toDp() } + cellPadding
    }
    val totalMin = minWidths.fold(0.dp) { acc, w -> acc + w }
    // Контент шире экрана (редко) — масштабируем пропорционально, чтобы не было переполнения.
    if (totalMin >= totalWidth) {
        val factor = totalWidth.value / totalMin.value
        return minWidths.map { (it.value * factor).dp }
    }
    val extra = totalWidth - totalMin
    val sumMin = minWidths.fold(0f) { acc, w -> acc + w.value }
    return minWidths.map { it + extra * (it.value / sumMin) }
}

@Composable
private fun TableRow(cells: List<String>, isHeader: Boolean, widths: List<Dp>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        cells.forEachIndexed { index, cell ->
            Box(
                modifier = Modifier
                    .width(widths.getOrElse(index) { 0.dp })
                    .padding(horizontal = Dimens.spaceSmall, vertical = Dimens.spaceSmall),
            ) {
                MarkdownText(
                    text = cell,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isHeader) Accent else TextPrimary,
                    fontWeight = if (isHeader) FontWeight.Bold else null,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun CalloutBlock(block: TheoryBlock.Callout) {
    val accentColor = when (block.variant) {
        CalloutVariant.TRAP -> IncorrectRed
        CalloutVariant.WARNING -> Highlight
        CalloutVariant.FORMULA -> Highlight
        CalloutVariant.TIP -> CorrectGreen
        CalloutVariant.NOTE -> TextSecondary
    }
    val monospace = if (block.variant == CalloutVariant.FORMULA) FontFamily.Monospace else null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        // Шапка плашки: ярлык по центру + цветная полоса-разделитель (того же цвета варианта).
        if (block.label.isNotBlank()) {
            MarkdownText(
                text = block.label,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.cardPadding)
                    .padding(top = Dimens.cardPadding, bottom = Dimens.spaceSmall),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.calloutDividerInset)
                    .height(Dimens.calloutDividerThickness)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(accentColor),
            )
        }
        // Тело плашки — вложенные блоки (абзацы/список/таблица).
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        ) {
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