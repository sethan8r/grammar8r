package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TRUE_FALSE — ровно 5 предложений, отметить верно/неверно.
 * `statements` — JSON-`String` (en, ru, isTrue), разбор в domain.
 * Схема — exercise_templates.md → `TrueFalseExercise`.
 */
@Entity(tableName = "true_false_exercises")
data class TrueFalseExercise(
    @PrimaryKey val id: Int,
    val statements: String,
    val explanation: String
)