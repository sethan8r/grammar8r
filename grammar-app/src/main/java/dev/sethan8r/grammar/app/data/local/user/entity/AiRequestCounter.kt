package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Дневной счётчик AI-запросов для проверки лимита тира (сброс 00:00 MSK по дате). Минимальная
 * дневная форма по образцу `DailyStats.date`; серверный учёт лимитов — Фаза 4.
 */
@Entity(tableName = "ai_request_counter")
data class AiRequestCounter(
    @PrimaryKey val date: String,
    val count: Int
)