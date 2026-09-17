package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FIND_THE_ODD — ровно 4 элемента, найти лишний по правилу.
 * `items` — JSON-`String` (text, isOdd), разбор в domain.
 * Схема — exercise_templates.md → `FindTheOddExercise`.
 */
@Entity(tableName = "find_the_odd_exercises")
data class FindTheOddExercise(
    @PrimaryKey val id: Int,
    val groupDescription: String,
    val items: String,
    val explanation: String
)