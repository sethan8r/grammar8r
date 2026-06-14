package dev.sethan8r.grammar.app.data.local.content.entity.theory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * «Раздел» — необязательный уровень группировки между списком тем и темами (напр. «Устройство
 * языка»). Вложенность ровно один уровень: раздел → темы → микротемы → карточки.
 * Источник правды по схеме — db_schema.md → `GrammarTopicCategory`.
 */
@Entity(tableName = "grammar_topic_categories")
data class GrammarTopicCategory(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "order") val order: Int
)