package dev.sethan8r.grammar.app.domain.usecase.theory

import dev.sethan8r.grammar.app.domain.model.theory.TheoryBlock

/**
 * Сворачивает блоки карточки в сплошной текст для AI-промта: модели нужен смысл правила, а не
 * вёрстка. Разметка блоков теряется намеренно — таблица становится строками через « — »,
 * плашка-врезка отдаёт ярлык и тело, разделители выпадают.
 *
 * Чистый Kotlin — тестируется без Android.
 */
object TheoryPlainText {

    fun render(blocks: List<TheoryBlock>): String =
        blocks.mapNotNull(::renderBlock).joinToString(separator = "\n\n")

    private fun renderBlock(block: TheoryBlock): String? = when (block) {
        is TheoryBlock.Paragraph -> block.text
        is TheoryBlock.Heading -> block.text
        is TheoryBlock.BulletList -> block.items.joinToString("\n") { "- $it" }
        is TheoryBlock.Table -> (listOf(block.header) + block.rows)
            .joinToString("\n") { row -> row.joinToString(" — ") }

        is TheoryBlock.Callout -> listOfNotNull(
            block.label.takeIf { it.isNotBlank() },
            render(block.blocks).takeIf { it.isNotBlank() },
        ).joinToString(": ").takeIf { it.isNotBlank() }

        TheoryBlock.Divider -> null
    }
}
