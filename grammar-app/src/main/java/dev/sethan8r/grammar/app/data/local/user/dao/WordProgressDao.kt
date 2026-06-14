package dev.sethan8r.grammar.app.data.local.user.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.sethan8r.grammar.app.data.local.user.entity.UserCategorySettings
import dev.sethan8r.grammar.app.data.local.user.entity.UserWordProgress
import dev.sethan8r.grammar.app.domain.model.WordTable
import kotlinx.coroutines.flow.Flow

/**
 * Состояние слов и настройки категорий в user.db. Базовый набор — расширяется в Фазе 2 (блок слов).
 */
@Dao
interface WordProgressDao {

    @Upsert
    suspend fun upsertWordProgress(progress: UserWordProgress)

    @Query("SELECT * FROM user_word_progress WHERE source = :source AND wordId = :wordId")
    suspend fun getWordProgress(source: WordTable, wordId: Int): UserWordProgress?

    @Query("SELECT * FROM user_word_progress WHERE source = :source")
    fun getWordProgressBySource(source: WordTable): Flow<List<UserWordProgress>>

    @Upsert
    suspend fun upsertCategorySettings(settings: UserCategorySettings)

    @Query("SELECT * FROM user_category_settings")
    fun getCategorySettings(): Flow<List<UserCategorySettings>>
}