package dev.sethan8r.grammar.app.data.local.user.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.sethan8r.grammar.app.data.local.user.entity.AiRequestCounter
import dev.sethan8r.grammar.app.data.local.user.entity.DailyStats
import kotlinx.coroutines.flow.Flow

/**
 * Дневная статистика и счётчик AI-запросов в user.db. Базовый набор — расширяется при реализации
 * экрана Статистика / лимитов (D2). `AiRequestCounter` — provisional (см. Entity).
 */
@Dao
interface StatsDao {

    @Upsert
    suspend fun upsertDailyStats(stats: DailyStats)

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getDailyStats(date: String): DailyStats?

    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllDailyStats(): Flow<List<DailyStats>>

    @Upsert
    suspend fun upsertRequestCounter(counter: AiRequestCounter)

    @Query("SELECT * FROM ai_request_counter WHERE date = :date")
    suspend fun getRequestCounter(date: String): AiRequestCounter?
}