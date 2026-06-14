package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DIALOG_RESTORE — диалог с пропуском (ровно одна реплика null), выбрать правильную реплику из 3.
 * `lines` — JSON-`String` (speaker, text|null), `options` — JSON-`String` (text, isCorrect). Разбор в domain.
 * Схема — exercise_templates.md → `DialogRestoreExercise`.
 */
@Entity(tableName = "dialog_restore_exercises")
data class DialogRestoreExercise(
    @PrimaryKey val id: Int,
    val lines: String,
    val options: String,
    val explanation: String
)