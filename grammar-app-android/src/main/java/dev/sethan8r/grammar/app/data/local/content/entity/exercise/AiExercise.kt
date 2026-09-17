package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.sethan8r.grammar.core.model.exercise.AiExerciseWordsSource
import dev.sethan8r.grammar.shared.AiExerciseInputMode

/**
 * AI-упражнение — клиентская часть. Промт и профиль конфигурации хранятся на сервере
 * (`ai_exercise_prompts`), клиент шлёт только `id`. Источник правды по схеме — db_schema.md →
 * `AiExercise` (раздел «AI-упражнения делятся на два выхода»).
 *
 * Натуральный строковый ключ `id` (серверный ключ промта) + числовой FK `cardId` (как `GrammarCard.id`).
 */
@Entity(
    tableName = "ai_exercises",
    indices = [Index("cardId")]
)
data class AiExercise(
    @PrimaryKey val id: String,
    val cardId: Int,
    val title: String,
    val userInstruction: String,
    val inputMode: AiExerciseInputMode,
    val wordsSource: AiExerciseWordsSource
)