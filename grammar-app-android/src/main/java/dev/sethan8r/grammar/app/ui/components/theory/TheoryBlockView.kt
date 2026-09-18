package dev.sethan8r.grammar.app.ui.components.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlin.math.min
import dev.sethan8r.grammar.core.model.theory.CalloutVariant
import dev.sethan8r.grammar.core.model.theory.DialogLine
import dev.sethan8r.grammar.core.model.theory.TheoryBlock
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Highlight
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
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
 *
 * [containerColor] — подложка таблиц и плашек. Она должна отличаться от поверхности, на которой лежат
 * блоки: на фоне экрана это [CardBackground], внутри окна цвета [CardBackground] — [Background].
 */
@Composable
fun TheoryBlocks(
    blocks: List<TheoryBlock>,
    modifier: Modifier = Modifier,
    containerColor: Color = CardBackground,
) {
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
                is TheoryBlock.Dialog -> DialogBlock(block)
                is TheoryBlock.Callout -> CalloutBlock(block, containerColor)
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

// Пузырь реплики: «хвостик» — прямой угол у своего края (справа снизу у нас, слева сверху у собеседника).
private val selfBubbleShape = RoundedCornerShape(
    topStart = Dimens.cornerCard,
    topEnd = Dimens.cornerCard,
    bottomStart = Dimens.cornerCard,
    bottomEnd = Dimens.cornerSmall,
)

private val otherBubbleShape = RoundedCornerShape(
    topStart = Dimens.cornerSmall,
    topEnd = Dimens.cornerCard,
    bottomStart = Dimens.cornerCard,
    bottomEnd = Dimens.cornerCard,
)

/**
 * Диалог-чат: наши реплики (`@Me`) — справа, залиты акцентом; реплики собеседников — слева на
 * [Elevated] с именем внутри пузыря. Весь диалог лежит в общем фрейме — так кусок переписки читается
 * одним блоком теории, а не россыпью пузырей поверх карточки.
 */
@Composable
private fun DialogBlock(block: TheoryBlock.Dialog) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(Dimens.outlineWidth, Inactive, RoundedCornerShape(Dimens.cornerCard))
            .padding(Dimens.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
    ) {
        block.lines.forEach { DialogLineRow(it) }
    }
}

/**
 * Одна реплика. Имя говорящего стоит ПЕРВОЙ СТРОКОЙ ВНУТРИ пузыря (а не подписью над ним), иначе
 * подпись раздвигает соседние реплики и вертикальный ритм чата становится рваным.
 */
@Composable
private fun DialogLineRow(line: DialogLine) {
    val textColor = if (line.isSelf) Background else TextPrimary
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (line.isSelf) Arrangement.End else Arrangement.Start,
    ) {
        // Пустое поле у противоположного края: пузырь тянется по тексту, но `fill = false`
        // не даёт ему занять всю ширину — иначе чат читается как обычные абзацы.
        if (line.isSelf) Spacer(Modifier.width(Dimens.dialogBubbleGutter))
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(if (line.isSelf) selfBubbleShape else otherBubbleShape)
                .background(if (line.isSelf) Accent else Elevated)
                .padding(
                    start = Dimens.spaceLarge,
                    end = Dimens.spaceLarge,
                    // Строка имени сама даёт воздух сверху — верхний отступ под ней поджимаем.
                    top = if (line.isSelf) Dimens.spaceMedium else Dimens.spaceSmall,
                    bottom = Dimens.spaceMedium,
                ),
        ) {
            // Своё имя не пишем: сторона и цвет пузыря и так говорят, кто это.
            if (!line.isSelf) {
                Text(
                    text = line.speaker,
                    modifier = Modifier.padding(bottom = Dimens.spaceTiny),
                    color = Accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            MarkdownText(text = line.text, color = textColor)
            line.note?.let {
                DialogNote(
                    text = it,
                    color = textColor,
                    modifier = Modifier.padding(top = Dimens.spaceSmall),
                )
            }
        }
        if (!line.isSelf) Spacer(Modifier.width(Dimens.dialogBubbleGutter))
    }
}

/** Ярлык хода разговора внутри реплики: тот же цвет, мельче основного текста, рамка без заливки. */
@Composable
private fun DialogNote(text: String, color: Color, modifier: Modifier = Modifier) {
    MarkdownText(
        text = text,
        modifier = modifier
            .border(
                width = Dimens.outlineWidth,
                color = color.copy(alpha = Alphas.dialogNoteBorder),
                shape = RoundedCornerShape(Dimens.cornerSmall),
            )
            .padding(horizontal = Dimens.spaceSmall, vertical = Dimens.spaceTiny),
        color = color.copy(alpha = Alphas.dialogNoteText),
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )
}

private val WHITESPACE = Regex("\\s+")

// Одиночное слово-ярлык длиннее — режем сбалансированно (≈ пополам), чтобы колонка под короткий
// контент не была широкой из-за длинного заголовка. Переноса по слогам в Compose нет → делим по символам.
private const val MAX_WORD_LEN = 9

// Ниже этого куска слово не режем никогда: 1–2 буквы на отдельной строке — брак.
private const val MIN_CHUNK_LEN = 3

// Запас к замеренной ширине колонки: гасит округления px↔dp (замер → Dp → обратно в px в
// Modifier.width), из-за которых слову могло не хватить долей пикселя и последняя буква переносилась.
private val MEASURE_SLACK = 1.dp

// Боковой отступ ячейки таблицы. Его же закладывает measureColumn — разъедутся, и замер колонки
// разойдётся с рендером: слово не влезет и порежется.
private val TABLE_CELL_PADDING_H = Dimens.spaceMedium

/**
 * Таблица-реестр: шапка — контурная капсула без заливки, каждая строка — своя залитая капсула,
 * между ними зазор вместо линий. Колонки внутри капсулы делит вертикальная полоса во всю её высоту.
 */
@Composable
private fun TableBlock(block: TheoryBlock.Table) {
    val columnCount = maxOf(block.header.size, block.rows.maxOfOrNull { it.size } ?: 0)
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    // Измерять надо ТЕМ ЖЕ стилем, каким рендерит Text (LocalTextStyle несёт шрифт/letterSpacing
    // темы) — «голый» TextStyle мерил уже, слово выходило на экране шире замера и рвалось.
    val baseStyle = LocalTextStyle.current

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        // Полосы-разделители стоят МЕЖДУ колонками, поэтому их ширину вычитаем до раскладки —
        // иначе сумма колонок с полосами вылезет за капсулу.
        val totalWidth = maxWidth - Dimens.outlineWidth * (columnCount - 1).coerceAtLeast(0)
        // Раскладка (текст ячеек + ширины колонок) считается один раз на (таблица + ширина).
        val layout = remember(block, totalWidth, baseStyle) {
            layoutTable(block.header, block.rows, columnCount, totalWidth, baseStyle, measurer, density)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
        ) {
            if (layout.header.isNotEmpty()) {
                TableCapsule(cells = layout.header, widths = layout.widths, isHeader = true)
            }
            layout.rows.forEach { row ->
                TableCapsule(cells = row, widths = layout.widths, isHeader = false)
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
 * Строит раскладку таблицы тремя проходами, от самого щадящего к самому жёсткому:
 * 1. мерим текст как есть — влезает нерезаным, значит ничего не делим;
 * 2. не влезает — делим одиночные слова-ярлыки ([wrapLongWords]) и меряем заново;
 * 3. всё ещё не влезает — прицельно ужимаем колонки ([squeezeColumns]), иначе колонкам досталось бы
 *    меньше их минимума и Compose доломал бы слова по буквам где попало.
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
    if (wrappedMetrics.totalMin <= totalWidth) {
        return TableLayout(wrappedHeader, wrappedRows, distributeWidths(wrappedMetrics, totalWidth))
    }
    return squeezeColumns(
        header, rows, wrappedHeader, wrappedRows, wrappedMetrics,
        columnCount, totalWidth, baseStyle, measurer, density,
    )
}

/**
 * Делит слово на сбалансированные куски (длины отличаются не больше чем на букву), каждый не короче
 * [MIN_CHUNK_LEN]: число частей ограничено так, что сиротская 1–2-буквенная строка невозможна.
 * Слово, которое нельзя поделить с соблюдением порога, возвращается целиком.
 */
private fun splitWord(word: String, maxLen: Int): List<String> {
    if (word.length <= maxLen) return listOf(word)
    val parts = min(ceil(word.length / maxLen.toDouble()).toInt(), word.length / MIN_CHUNK_LEN)
    if (parts <= 1) return listOf(word)
    val base = word.length / parts
    val longerParts = word.length % parts
    var start = 0
    return (0 until parts).map { index ->
        val size = base + if (index < longerParts) 1 else 0
        word.substring(start, start + size).also { start += size }
    }
}

/**
 * Делит ОДИНОЧНОЕ слово-ярлык длиннее [MAX_WORD_LEN] на части (через `\n`). В многословных ячейках
 * (предложениях) слова не трогаем — они переносятся по пробелам. Жирные токены не трогаем: деление
 * сломало бы разметку.
 */
private fun wrapLongWords(cell: String): String {
    val trimmed = cell.trim()
    val isSingleWord = trimmed.isNotEmpty() && trimmed.none { it.isWhitespace() }
    if (!isSingleWord || trimmed.length <= MAX_WORD_LEN || trimmed.contains('*')) return cell
    return splitWord(trimmed, MAX_WORD_LEN).joinToString("\n")
}

/** Жёсткое деление третьего прохода: режет КАЖДОЕ слово ячейки длиннее [maxLen], включая предложения. */
private fun wrapEveryWord(cell: String, maxLen: Int): String =
    cell.trim().split(WHITESPACE).joinToString(" ") { word ->
        if ('*' in word) word else splitWord(word, maxLen).joinToString("\n")
    }

/**
 * Таблица не влезает даже с делением ярлыков. Ужимаем колонки прицельно: сначала те, у которых
 * маленький ideal (колонки-ярлыки — потеря ширины им дешевле всего), в последнюю очередь колонки
 * с предложениями. Каждой понижаем допустимую длину куска, пока таблица не влезет; как только
 * влезла — останавливаемся, поэтому остальные колонки остаются нетронутыми и забирают освободившееся
 * место обычной раздачей по дефициту.
 *
 * Режем всегда ИСХОДНЫЙ текст ячейки, а не результат второго прохода: перенос там уже проставлен
 * как `\n`, и повторное деление склеило бы куски слова через пробел.
 */
private fun squeezeColumns(
    header: List<String>,
    rows: List<List<String>>,
    wrappedHeader: List<String>,
    wrappedRows: List<List<String>>,
    metrics: ColumnMetrics,
    columnCount: Int,
    totalWidth: Dp,
    baseStyle: TextStyle,
    measurer: TextMeasurer,
    density: Density,
): TableLayout {
    val outHeader = wrappedHeader.toMutableList()
    val outRows = wrappedRows.map { it.toMutableList() }
    val mins = metrics.min.toMutableList()
    val ideals = metrics.ideal.toMutableList()
    fun totalMin() = mins.fold(0.dp) { acc, w -> acc + w }

    // Ставит колонке текст с делением по maxLen (null — вернуть текст второго прохода) и переменяет её.
    fun applyColumn(column: Int, maxLen: Int?) {
        outHeader[column] = maxLen
            ?.let { len -> header.getOrNull(column)?.let { wrapEveryWord(it, len) } }
            ?: wrappedHeader[column]
        outRows.forEachIndexed { index, row ->
            if (column < row.size) {
                row[column] = maxLen
                    ?.let { len -> rows[index].getOrNull(column)?.let { wrapEveryWord(it, len) } }
                    ?: wrappedRows[index][column]
            }
        }
        val (columnMin, columnIdeal) = measureColumn(column, outHeader, outRows, baseStyle, measurer, density)
        mins[column] = columnMin
        ideals[column] = columnIdeal
    }

    for (column in (0 until columnCount).sortedBy { ideals[it].value }) {
        if (totalMin() <= totalWidth) break
        // Колонку жмём ровно до той степени, которая даёт выигрыш по ширине: как только таблица
        // влезла — останавливаемся, а если не влезла и на самых мелких кусках, оставляем САМОЕ
        // КРУПНОЕ деление из тех, что дали лучшую ширину, — мельчить его дальше уже бессмысленно.
        var bestMaxLen: Int? = null
        var bestMin = mins[column]
        var fitted = false
        for (maxLen in MAX_WORD_LEN - 1 downTo MIN_CHUNK_LEN) {
            applyColumn(column, maxLen)
            if (mins[column] < bestMin) {
                bestMin = mins[column]
                bestMaxLen = maxLen
            }
            if (totalMin() <= totalWidth) {
                fitted = true
                break
            }
        }
        if (!fitted) applyColumn(column, bestMaxLen)
    }

    val squeezed = ColumnMetrics(mins, ideals)
    return TableLayout(outHeader, outRows.map { it.toList() }, distributeWidths(squeezed, totalWidth))
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
    val measured = (0 until columnCount).map { measureColumn(it, header, rows, baseStyle, measurer, density) }
    return ColumnMetrics(min = measured.map { it.first }, ideal = measured.map { it.second })
}

/** Замер одной колонки: пара min (самое длинное слово) — ideal (самая длинная строка ячейки целиком). */
private fun measureColumn(
    column: Int,
    header: List<String>,
    rows: List<out List<String>>,
    baseStyle: TextStyle,
    measurer: TextMeasurer,
    density: Density,
): Pair<Dp, Dp> {
    val bodyStyle = baseStyle.merge(TextStyle(fontSize = 14.sp, lineHeight = 20.sp))
    val headerStyle = bodyStyle.merge(TextStyle(fontWeight = FontWeight.Bold))
    val cellPadding = TABLE_CELL_PADDING_H * 2
    val styledRows = listOf(header to headerStyle) + rows.map { it to bodyStyle }

    // Ширина самого широкого куска ячеек колонки; куски задаёт split (слова либо готовые строки).
    // Кусок с `*` рендерится жирным спаном — меряем его жирным, иначе недомер и перенос букв.
    fun widestPiece(split: (String) -> List<String>): Dp {
        val maxPx = styledRows.maxOf { (row, style) ->
            val cell = row.getOrNull(column) ?: return@maxOf 0
            split(cell).maxOfOrNull { piece ->
                val pieceStyle = if ('*' in piece) headerStyle else style
                measurer.measure(AnnotatedString(piece.replace("*", "").trim()), pieceStyle).size.width
            } ?: 0
        }
        return with(density) { maxPx.toDp() } + cellPadding + MEASURE_SLACK
    }

    return widestPiece { it.split(WHITESPACE) } to widestPiece { it.split('\n') }
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

/**
 * Одна капсула таблицы. Шапка идёт контуром без заливки и мельче тела, строка — залитой плашкой;
 * первая колонка держит ключ строки, поэтому чуть плотнее по весу.
 */
@Composable
private fun TableCapsule(cells: List<String>, widths: List<Dp>, isHeader: Boolean) {
    val shape = RoundedCornerShape(Dimens.cornerSmall)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (isHeader) Modifier.border(Dimens.outlineWidth, Inactive, shape)
                else Modifier.background(Elevated)
            )
            // Полоса-разделитель тянется во всю высоту капсулы — значит высота Row известна до неё.
            .height(IntrinsicSize.Min),
    ) {
        cells.forEachIndexed { index, cell ->
            MarkdownText(
                text = cell,
                modifier = Modifier
                    .width(widths.getOrElse(index) { 0.dp })
                    .padding(horizontal = TABLE_CELL_PADDING_H, vertical = Dimens.spaceSmall),
                color = if (isHeader) Accent else TextPrimary,
                fontWeight = if (isHeader || index == 0) FontWeight.Medium else null,
                fontSize = if (isHeader) 13.sp else 14.sp,
                lineHeight = if (isHeader) 16.sp else 20.sp,
                textAlign = if (isHeader) TextAlign.Center else TextAlign.Start,
            )
            if (index < cells.lastIndex) {
                Box(
                    modifier = Modifier
                        .width(Dimens.outlineWidth)
                        .fillMaxHeight()
                        .background(Inactive),
                )
            }
        }
    }
}

@Composable
private fun CalloutBlock(block: TheoryBlock.Callout, containerColor: Color) {
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
            .background(containerColor),
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
            block.blocks.forEach { CalloutBodyBlock(it, monospace, containerColor) }
        }
    }
}

/** Рендер одного блока внутри плашки (тело callout не содержит divider/heading). */
@Composable
private fun CalloutBodyBlock(block: TheoryBlock, monospace: FontFamily?, containerColor: Color) {
    when (block) {
        is TheoryBlock.Paragraph -> MarkdownText(block.text, fontFamily = monospace)
        is TheoryBlock.BulletList -> ListBlock(block)
        is TheoryBlock.Table -> TableBlock(block)
        is TheoryBlock.Heading -> MarkdownText(
            text = block.text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        is TheoryBlock.Dialog -> DialogBlock(block)
        is TheoryBlock.Callout -> CalloutBlock(block, containerColor)
        TheoryBlock.Divider -> Unit
    }
}