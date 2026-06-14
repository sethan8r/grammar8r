package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Пользовательские настройки категории слов курса. Создаётся лениво при изменении.
 * Поля перенесены из `course_categories` (контент) — здесь живёт только состояние.
 * ⚠️ `isPriority`: одновременно активна только одна категория (см. db_schema → `course_categories`).
 * Источник правды по схеме — db_schema.md → `UserCategorySettings`.
 */
@Entity(tableName = "user_category_settings")
data class UserCategorySettings(
    @PrimaryKey val categoryId: String,
    val isSelected: Boolean,
    val isPriority: Boolean
)