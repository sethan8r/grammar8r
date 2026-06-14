package dev.sethan8r.grammar.app.domain.model.theory

/**
 * Типизированный блок теории — доменное представление одного элемента карточки.
 *
 * В content.db теория лежит сырой JSON-строкой (массив блоков); разбор строки в эти типы делает
 * data-маппер ([dev.sethan8r.grammar.app.data.mapper.TheoryContentMapper]) — сериализация в домен
 * не протекает. Каталог типов соответствует db_schema.md → «Блоки теории».
 */
sealed interface TheoryBlock {

    /** Абзац прозы. Может содержать инлайн `**жирный**` / `*курсив*`. */
    data class Paragraph(val text: String) : TheoryBlock

    /** Жирный мини-подзаголовок секции внутри карточки. */
    data class Heading(val text: String) : TheoryBlock

    /** Список — маркированный (`ordered == false`) или нумерованный. */
    data class BulletList(val ordered: Boolean, val items: List<String>) : TheoryBlock

    /** Таблица: строка заголовков + строки данных (каждая — список ячеек). */
    data class Table(val header: List<String>, val rows: List<List<String>>) : TheoryBlock

    /**
     * Плашка-врезка (ловушка/важно/совет/формула). [label] — ярлык, [blocks] — тело (может быть
     * абзацами/списком/таблицей — поэтому это вложенные блоки, а не строка).
     */
    data class Callout(val variant: CalloutVariant, val label: String, val blocks: List<TheoryBlock>) : TheoryBlock

    /**
     * Тонкая горизонтальная линия — разделитель смысловых частей теории. В MD автор ставит `---`;
     * конвертер пока кладёт это параграфом `"---"`, маппер распознаёт HR-строку и отдаёт сюда.
     */
    data object Divider : TheoryBlock
}

/** Вид плашки [TheoryBlock.Callout]. Определяет цвет/оформление при рендере. */
enum class CalloutVariant { TRAP, WARNING, TIP, FORMULA, NOTE }