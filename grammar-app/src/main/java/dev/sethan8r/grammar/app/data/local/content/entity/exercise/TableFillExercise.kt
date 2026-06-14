package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TABLE_FILL — таблица с пропусками (до 7 строк), вписать ответ в каждую.
 * `rows` — JSON-`String` (hint, answer), разбор в domain.
 * Схема — exercise_templates.md → `TableFillExercise`.
 */
@Entity(tableName = "table_fill_exercises")
data class TableFillExercise(
    @PrimaryKey val id: Int,
    val taskDescription: String,
    val rows: String,
    val explanation: String
)