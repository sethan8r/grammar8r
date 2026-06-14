package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Прогресс по хардкодным упражнениям карточки. Пишется только при полном прохождении (N/N).
 * Логика попыток — UI-стейт в ViewModel, сюда не пишется.
 * Источник правды по схеме — db_schema.md → `UserCardHardcodeStats`.
 */
@Entity(tableName = "user_card_hardcode_stats")
data class UserCardHardcodeStats(
    @PrimaryKey val cardId: Int,
    val isCompleted: Boolean
)