package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Прогресс по AI-упражнению. Создаётся лениво при первом повторении; нет строки → qRep = 0.
 * Источник правды по схеме — db_schema.md → `UserAiExerciseStats`.
 */
@Entity(tableName = "user_ai_exercise_stats")
data class UserAiExerciseStats(
    @PrimaryKey val exerciseId: String,
    val qRep: Int
)