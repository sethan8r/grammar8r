package dev.sethan8r.grammar.app.data.local.content.entity.theory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Тема — верхний уровень дерева теории. Показывается напрямую (`categoryId == null`) либо свёрнутой
 * внутри раздела [GrammarTopicCategory]. `isPretopic == true` только у предтемы «Основы».
 * Источник правды по схеме — db_schema.md → `GrammarTopic`.
 *
 * Между БД нет FK: `categoryId` — индекс, не `@ForeignKey` (раздел тоже в content.db, но FK
 * сознательно не объявляем, чтобы осиротевшие ссылки не роняли вставку сида).
 */
@Entity(tableName = "grammar_topics")
data class GrammarTopic(
    @PrimaryKey val id: Int,
    val title: String,
    @ColumnInfo(name = "order") val order: Int,
    val isPretopic: Boolean,
    val categoryId: Int?,
    val description: String?,
    /** Теги поиска через запятую, строчными; пользователю не показываются. Зонт всей темы. */
    val searchKeywords: String?
)