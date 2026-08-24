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
import androidx.compose.material3.LocalTextStyle
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

// Запас к замеренной ширине колонки: гасит округления px↔dp (замер → Dp → обратно в px в
// Modifier.width), из-за которых слову могло не хватить долей пикселя и последняя буква переносилась.
private val MEASURE_SLACK = 1.dp

@Composable
private fun TableBlock(block: TheoryBlock.Table) {
    val columnCount = maxOf(block.header.size, block.rows.maxOfOrNull { it.size } ?: 0)
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    // Измерять надо ТЕМ ЖЕ стилем, каким рендерит Text (LocalTextStyle несёт шрифт/letterSpacing
    // темы) — «голый» TextStyle мерил уже, слово выходило на экране шире замера и рвалось.
    val baseStyle = LocalTextStyle.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        val totalWidth = maxWidth
        // Раскладка (текст ячеек + ширины колонок) считается один раз на (таблица + ширина).
        val layout = remember(block, totalWidth, baseStyle) {
            layoutTable(block.header, block.rows, columnCount, totalWidth, baseStyle, measurer, density)
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if (layout.header.isNotEmpty()) {
                TableRow(cells = layout.header, isHeader = true, widths = layout.widths)
                HorizontalDivider(color = Inactive)
            }
            layout.rows.forEachIndexed { index, row ->
                TableRow(cells = row, isHeader = false, widths = layout.widths)
                if (index < layout.rows.lastIndex) HorizontalDivider(color = Inactive)
            }
        }
    }
}

/** Готовая раскладка таблицы: текст ячеек (возможно с переносами) и ширины колонок. */
private data class TableLayout(
    val header: List<String>,
    val rows: List<List<String>>,
    val widths: List<Dp>,
)

/** Замеры колонок: min — самое длинное слово, ideal — самая длинная строка ячейки целиком. */
private data class ColumnMetrics(val min: List<Dp>, val ideal: List<Dp>) {
    val totalMin: Dp = min.fold(0.dp) { acc, w -> acc + w }
}

/**
 * Строит раскладку таблицы. Сначала мерит текст как есть: если таблица влезает в ширину экрана
 * нерезаной — длинные слова не делим (делить их незачем, соседним колонкам место не нужно).
 * Не влезает — делим одиночные слова-ярлыки ([wrapLongWords]) и меряем заново.
 */
private fun layoutTable(
    header: List<String>,
    rows: List<List<String>>,
    columnCount: Int,
    totalWidth: Dp,
    baseStyle: TextStyle,
    measurer: TextMeasurer,
    density: Density,
): TableLayout {
    val metrics = measureColumns(header, rows, columnCount, baseStyle, measurer, density)
    if (metrics.totalMin <= totalWidth) {
        return TableLayout(header, rows, distributeWidths(metrics, totalWidth))
    }
    val wrappedHeader = header.map(::wrapLongWords)
    val wrappedRows = rows.map { row -> row.map(::wrapLongWords) }
    val wrappedMetrics = measureColumns(wrappedHeader, wrappedRows, columnCount, baseStyle, measurer, density)
    return TableLayout(wrappedHeader, wrappedRows, distributeWidths(wrappedMetrics, totalWidth))
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
 * Два замера колонок ([TextMeasurer], как auto-layout таблиц в браузерах): min — самое длинное
 * слово (гарантия, что слова НЕ рвутся по буквам), ideal — самая длинная строка ячейки целиком
 * (шире колонке уже не нужно). Заголовок меряется жирным, тело — обычным, оба — поверх [baseStyle]
 * рендера (шрифт/letterSpacing темы), иначе замер уже реальной ширины и слово рвётся посреди букв.
 * Плюс [MEASURE_SLACK] на округления px↔dp при обратной конверсии замера в `Modifier.width`.
 */
private fun measureColumns(
    header: List<String>,
    rows: List<List<String>>,
    columnCount: Int,
    baseStyle: TextStyle,
    measurer: TextMeasurer,
    density: Density,
): ColumnMetrics {
    val bodyStyle = baseStyle.merge(TextStyle(fontSize = 14.sp, lineHeight = 20.sp))
    val headerStyle = bodyStyle.merge(TextStyle(fontWeight = FontWeight.Bold))
    val cellPadding = Dimens.spaceSmall * 2
    val styledRows = listOf(header to headerStyle) + rows.map { it to bodyStyle }

    // Ширина самого широкого куска ячеек колонки; куски задаёт split (слова либо готовые строки).
    // Кусок с `*` рендерится жирным спаном — меряем его жирным, иначе недомер и перенос букв.
    fun widestPiece(column: Int, split: (String) -> List<String>): Dp {
        val maxPx = styledRows.maxOf { (row, style) ->
            val cell = row.getOrNull(column) ?: return@maxOf 0
            split(cell).maxOfOrNull { piece ->
                val pieceStyle = if ('*' in piece) headerStyle else style
                measurer.measure(AnnotatedString(piece.replace("*", "").trim()), pieceStyle).size.width
            } ?: 0
        }
        return with(density) { maxPx.toDp() } + cellPadding + MEASURE_SLACK
    }

    return ColumnMetrics(
        min = (0 until columnCount).map { widestPiece(it) { cell -> cell.split(WHITESPACE) } },
        ideal = (0 until columnCount).map { widestPiece(it) { cell -> cell.split('\n') } },
    )
}

/**
 * Раздаёт ширину экрана по колонкам. Каждой гарантируется её min, остаток — пропорционально
 * дефициту (ideal − min) с потолком ideal: колонка, чей контент уже влезает, лишнего не забирает.
 */
private fun distributeWidths(metrics: ColumnMetrics, totalWidth: Dp): List<Dp> {
    val (minWidths, idealWidths) = metrics
    val totalMin = metrics.totalMin
    // Контент шире экрана (редко) — масштабируем пропорционально, чтобы не было переполнения.
    if (totalMin >= totalWidth) {
        val factor = totalWidth.value / totalMin.value
        return minWidths.map { (it.value * factor).dp }
    }

    val extra = totalWidth - totalMin
    val deficits = minWidths.indices.map { (idealWidths[it] - minWidths[it]).coerceAtLeast(0.dp) }
    val totalDeficit = deficits.fold(0.dp) { acc, w -> acc + w }
    // Места меньше суммарного дефицита — делим по дефициту (доля каждой ≤ её дефицита, потолок соблюдён).
    if (extra <= totalDeficit && totalDeficit > 0.dp) {
        return minWidths.mapIndexed { i, w -> w + extra * (deficits[i].value / totalDeficit.value) }
    }
    // Всем хватает до ideal (каждая ячейка в одну строку) — излишек добиваем пропорционально ideal,
    // чтобы таблица по-прежнему занимала всю ширину.
    val leftover = extra - totalDeficit
    val sumIdeal = idealWidths.fold(0f) { acc, w -> acc + w.value }
    if (sumIdeal <= 0f) return minWidths
    return idealWidths.map { it + leftover * (it.value / sumIdeal) }
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