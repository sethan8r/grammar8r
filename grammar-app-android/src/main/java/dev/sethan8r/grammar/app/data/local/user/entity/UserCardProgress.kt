package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Прогресс по карточке. Создаётся лениво при первом открытии карточки.
 * Источник правды по схеме — db_schema.md → `UserCardProgress`.
 */
@Entity(tableName = "user_card_progress")
data class UserCardProgress(
    @PrimaryKey val cardId: Int,
    val isCompleted: Boolean
)