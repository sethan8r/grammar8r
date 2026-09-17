package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Дневная статистика (только локально, не синкается). `date` — "YYYY-MM-DD" по MSK (сброс 00:00 MSK).
 * `topicsVisited` — JSON-`String` (список ID), разбор в domain. Источник правды — db_schema.md → `DailyStats`.
 */
@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey val date: String,
    val topicsVisited: String,
    val hardcodedCount: Int,
    val aiCount: Int,
    val avgScore: Float
)