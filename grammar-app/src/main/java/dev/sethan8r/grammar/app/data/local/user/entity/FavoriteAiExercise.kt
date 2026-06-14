package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Избранное AI-упражнение. `id` autoGenerate — сортировка ORDER BY id DESC (последнее первым).
 * UNIQUE-индекс на `exerciseId` запрещает добавить одно упражнение дважды.
 * Источник правды по схеме — db_schema.md → `FavoriteAiExercise`.
 */
@Entity(
    tableName = "favorite_ai_exercise",
    indices = [Index(value = ["exerciseId"], unique = true)]
)
data class FavoriteAiExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: String
)