package dev.sethan8r.grammar.app.data.local.content.entity.theory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Микротема — второй уровень дерева, раскрывается внутри темы.
 * Источник правды по схеме — db_schema.md → `GrammarMicrotopic`.
 */
@Entity(
    tableName = "grammar_microtopics",
    indices = [Index("topicId")]
)
data class GrammarMicrotopic(
    @PrimaryKey val id: Int,
    val topicId: Int,
    val title: String,
    @ColumnInfo(name = "order") val order: Int
)