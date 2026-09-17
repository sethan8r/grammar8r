package dev.sethan8r.grammar.app.data.local.content.entity.theory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Карточка — третий уровень дерева: теория + резюме + примеры + варианты уточнений.
 * Источник правды по схеме — db_schema.md → `GrammarCard`.
 *
 * `theory`, `examples`, `clarificationOptions` хранятся как сырой JSON-`String` (колонка TEXT) —
 * типизированный разбор (массив блоков теории, пары RU→EN) делает domain-маппер при чтении,
 * не Room. См. комментарий в Converters.kt.
 */
@Entity(
    tableName = "grammar_cards",
    indices = [Index("microtopicId")]
)
data class GrammarCard(
    @PrimaryKey val id: Int,
    val microtopicId: Int,
    val title: String,
    @ColumnInfo(name = "order") val order: Int,
    val theory: String,
    val theorySummary: String,
    val examples: String,
    val clarificationOptions: String
)