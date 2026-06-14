package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * MATCHING — 4–6 пар, соединить линиями (перемешивание на рантайме в UI).
 * `pairs` — JSON-`String` (left, right) — ПРАВИЛЬНЫЕ пары, разбор в domain.
 * Схема — exercise_templates.md → `MatchingExercise`.
 */
@Entity(tableName = "matching_exercises")
data class MatchingExercise(
    @PrimaryKey val id: Int,
    val taskDescription: String,
    val pairs: String,
    val explanation: String
)