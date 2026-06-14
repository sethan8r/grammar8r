package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Прогресс по микротеме. `isCompleted` НЕ сбрасывается при повторном прохождении (анти-чит).
 * Источник правды по схеме — db_schema.md → `UserMicrotopicProgress`.
 */
@Entity(tableName = "user_microtopic_progress")
data class UserMicrotopicProgress(
    @PrimaryKey val microtopicId: Int,
    val isCompleted: Boolean
)