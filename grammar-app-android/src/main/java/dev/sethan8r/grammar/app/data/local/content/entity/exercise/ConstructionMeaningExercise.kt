package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * CONSTRUCTION_MEANING — грамматическая конструкция, выбрать правильный RU смысл из 4.
 * `options` — JSON-`String` (ровно 4 варианта, один isCorrect), разбор в domain.
 * Схема — exercise_templates.md → `ConstructionMeaningExercise`.
 */
@Entity(tableName = "construction_meaning_exercises")
data class ConstructionMeaningExercise(
    @PrimaryKey val id: Int,
    val construction: String,
    val options: String,
    val explanation: String
)