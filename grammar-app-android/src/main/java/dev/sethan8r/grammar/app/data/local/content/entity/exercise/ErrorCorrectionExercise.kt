package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ERROR_CORRECTION — сломанное EN-предложение, выбрать правильный вариант (без RU в условии).
 * `options` — JSON-`String` (ровно 3 варианта, один/два isCorrect), разбор в domain.
 * Схема — exercise_templates.md → `ErrorCorrectionExercise`.
 */
@Entity(tableName = "error_correction_exercises")
data class ErrorCorrectionExercise(
    @PrimaryKey val id: Int,
    val wrongSentence: String,
    val options: String,
    val explanation: String
)