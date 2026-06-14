package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TRANSFORMATION — одна трансформация на ровно 3 примера (original → transformed).
 * `items` — JSON-`String` (original, transformed), разбор в domain.
 * Схема — exercise_templates.md → `TransformationExercise`.
 */
@Entity(tableName = "transformation_exercises")
data class TransformationExercise(
    @PrimaryKey val id: Int,
    val taskDescription: String,
    val items: String,
    val explanation: String
)