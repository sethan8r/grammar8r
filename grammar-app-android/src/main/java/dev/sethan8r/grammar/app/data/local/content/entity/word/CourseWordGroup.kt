package dev.sethan8r.grammar.app.data.local.content.entity.word

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Верхний уровень дерева Словаря «Слова курса» — раздел. Дерево:
 * course_word_groups → course_categories → course_words.
 * Источник правды по схеме — db_schema.md → `course_word_groups`.
 */
@Entity(tableName = "course_word_groups")
data class CourseWordGroup(
    @PrimaryKey val id: String,
    val nameRus: String,
    @ColumnInfo(name = "order") val order: Int
)