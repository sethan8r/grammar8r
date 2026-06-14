package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WORD_ARRANGEMENT — собрать предложение из перемешанных слов (есть дистракторы).
 * `words`/`distractors` — JSON-`String` (список объектов text+translation), разбор в domain.
 * Схема — exercise_templates.md → `WordArrangementExercise`. `id` уникален внутри типа.
 */
@Entity(tableName = "word_arrangement_exercises")
data class WordArrangementExercise(
    @PrimaryKey val id: Int,
    val situationRu: String,
    val correctSentence: String,
    val words: String,
    val distractors: String,
    val explanation: String
)