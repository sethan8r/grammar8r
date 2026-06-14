package dev.sethan8r.grammar.app.data.mapper

import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarCard
import dev.sethan8r.grammar.app.domain.model.theory.CalloutVariant
import dev.sethan8r.grammar.app.domain.model.theory.Example
import dev.sethan8r.grammar.app.domain.model.theory.TheoryBlock
import dev.sethan8r.grammar.app.domain.model.theory.TheoryCard
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Маппер `GrammarCard` (Entity, сырой JSON-`String`) → [TheoryCard] (домен, типизированные блоки).
 * Здесь и только здесь живёт разбор JSON теории (решение Шага B: разбор в маппере, не в Room).
 */
class TheoryContentMapper @Inject constructor(
    private val json: Json,
) {

    fun toTheoryCard(entity: GrammarCard): TheoryCard = TheoryCard(
        id = entity.id,
        microtopicId = entity.microtopicId,
        title = entity.title,
        order = entity.order,
        blocks = parseBlocks(entity.theory),
        summary = entity.theorySummary,
        examples = parseExamples(entity.examples),
        clarificationOptions = parseClarifications(entity.clarificationOptions),
    )

    private fun parseBlocks(raw: String): List<TheoryBlock> =
        json.decodeFromString<List<TheoryBlockJson>>(raw).map { it.toDomain() }

    private fun parseExamples(raw: String): List<Example> =
        json.decodeFromString<List<ExampleJson>>(raw).map { Example(ru = it.ru, en = it.en) }

    private fun parseClarifications(raw: String): List<String> =
        json.decodeFromString<List<String>>(raw)

    private fun TheoryBlockJson.toDomain(): TheoryBlock = when (this) {
        is ParagraphJson ->
            if (text.isHorizontalRule()) TheoryBlock.Divider else TheoryBlock.Paragraph(text)
        is HeadingJson -> TheoryBlock.Heading(text)
        is ListJson -> TheoryBlock.BulletList(ordered = ordered, items = items)
        is TableJson -> TheoryBlock.Table(header = header, rows = rows)
        is CalloutJson -> TheoryBlock.Callout(
            variant = variant.toVariant(),
            label = label,
            blocks = blocks.map { it.toDomain() },
        )
    }

    /** Строка-разделитель MD (вся строка из `---`/`***`/`___`, ≥3 символов). Инлайн `___` в прозе — не HR. */
    private fun String.isHorizontalRule(): Boolean =
        Regex("^(-{3,}|\\*{3,}|_{3,})$").matches(trim())

    private fun String.toVariant(): CalloutVariant = when (lowercase()) {
        "trap" -> CalloutVariant.TRAP
        "warning" -> CalloutVariant.WARNING
        "tip" -> CalloutVariant.TIP
        "formula" -> CalloutVariant.FORMULA
        else -> CalloutVariant.NOTE
    }
}