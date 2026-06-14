package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * CATEGORIZATION — распределить слова по 2–3 колонкам-категориям (6–15 элементов).
 * `categories` — JSON-`String` (title, items[]), разбор в domain.
 * Схема — exercise_templates.md → `CategorizationExercise`.
 */
@Entity(tableName = "categorization_exercises")
data class CategorizationExercise(
    @PrimaryKey val id: Int,
    val taskDescription: String,
    val categories: String,
    val explanation: String
)