package dev.sethan8r.grammar.app.data.mapper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data-слойные суррогаты для разбора сырого JSON полей `GrammarCard.theory`/`examples`/
 * `clarificationOptions`. Существуют только ради десериализации и тут же маппятся в доменные
 * модели ([dev.sethan8r.grammar.core.model.TheoryBlock] и т.д.) — kotlinx.serialization
 * в домен и в Room не протекает.
 *
 * Полиморфизм блоков — по полю-дискриминатору `type` (значение по умолчанию у kotlinx Json),
 * которое и пишет конвертер (`{"type":"paragraph",...}`). Подклассы sealed-иерархии
 * регистрируются автоматически на этапе компиляции — отдельный SerializersModule не нужен.
 */
@Serializable
sealed interface TheoryBlockJson

@Serializable
@SerialName("paragraph")
data class ParagraphJson(val text: String = "") : TheoryBlockJson

@Serializable
@SerialName("heading")
data class HeadingJson(val text: String = "") : TheoryBlockJson

@Serializable
@SerialName("list")
data class ListJson(
    val ordered: Boolean = false,
    val items: List<String> = emptyList(),
) : TheoryBlockJson

@Serializable
@SerialName("table")
data class TableJson(
    val header: List<String> = emptyList(),
    val rows: List<List<String>> = emptyList(),
) : TheoryBlockJson

@Serializable
@SerialName("callout")
data class CalloutJson(
    val variant: String = "",
    val label: String = "",
    val blocks: List<TheoryBlockJson> = emptyList(),
) : TheoryBlockJson

@Serializable
@SerialName("dialog")
data class DialogJson(val lines: List<DialogLineJson> = emptyList()) : TheoryBlockJson

/** Реплика диалога: говорящий, текст и необязательный ярлык хода разговора. */
@Serializable
data class DialogLineJson(
    val speaker: String = "",
    val text: String = "",
    val note: String? = null,
)

/** Пара RU→EN из поля `examples`. */
@Serializable
data class ExampleJson(val ru: String = "", val en: String = "")