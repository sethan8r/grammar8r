package dev.sethan8r.grammar.app.data.local.content.entity.word

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Категория слов курса — только КОНТЕНТНАЯ часть. `source` указывает, в какой таблице слова
 * категории: "course_words" или "irregular_verbs".
 *
 * ⚠️ Поля состояния (`isSelected`/`isPriority`) живут в user.db → `UserCategorySettings`, здесь их НЕТ.
 * Источник правды по схеме — db_schema.md → `course_categories`.
 */
@Entity(
    tableName = "course_categories",
    indices = [Index("groupId")]
)
data class CourseCategory(
    @PrimaryKey val id: String,
    val groupId: String,
    @ColumnInfo(name = "order") val order: Int,
    val nameRus: String,
    val source: String
)