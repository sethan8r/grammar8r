package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import dev.sethan8r.grammar.app.domain.model.ChoiceType

/**
 * Упражнение с выбором варианта: CHOICE / FORWARD_CHOICE / REVERSE_CHOICE (различает `choiceType`).
 * `options` — JSON-`String` (ровно 3 варианта, один isCorrect), разбор в domain.
 * Схема — exercise_templates.md → `MultipleChoiceExercise`. `id` уникален лишь внутри своего
 * `choiceType` (три трека канона `MultipleChoice·*`), поэтому PK — составной `(id, choiceType)`.
 */
@Entity(tableName = "multiple_choice_exercises", primaryKeys = ["id", "choiceType"])
data class MultipleChoiceExercise(
    val id: Int,
    val choiceType: ChoiceType,
    val prompt: String,
    val contextRu: String,
    val options: String,
    val explanation: String
)