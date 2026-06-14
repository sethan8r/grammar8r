package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TEXT_INPUT — вписать ответ вручную (case-insensitive), 3–5 пунктов в блоке.
 * `items` — JSON-`String` (sentence, contextRu, answer, alternatives), разбор в domain.
 * Схема — exercise_templates.md → `TextInputExercise`.
 */
@Entity(tableName = "text_input_exercises")
data class TextInputExercise(
    @PrimaryKey val id: Int,
    val items: String,
    val explanation: String
)