package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Дневной счётчик AI-запросов для проверки лимита тира (сброс 00:00 MSK по дате).
 *
 * ⚠️ provisional: db_schema.md держит `AiRequestCounter` в «незафиксированных» — финальная схема
 * проясняется в Шаге D2 (EntitlementsProvider) / Фазе 4 (серверный учёт лимитов). Заложена
 * минимальная дневная форма по образцу `DailyStats.date`. Зафиксировано в decision_log Шага B.
 */
@Entity(tableName = "ai_request_counter")
data class AiRequestCounter(
    @PrimaryKey val date: String,
    val count: Int
)