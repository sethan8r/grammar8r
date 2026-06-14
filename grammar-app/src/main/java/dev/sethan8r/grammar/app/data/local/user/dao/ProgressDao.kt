package dev.sethan8r.grammar.app.data.local.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.sethan8r.grammar.app.data.local.user.entity.FavoriteAiExercise
import dev.sethan8r.grammar.app.data.local.user.entity.UserAiExerciseStats
import dev.sethan8r.grammar.app.data.local.user.entity.UserCardHardcodeStats
import dev.sethan8r.grammar.app.data.local.user.entity.UserCardProgress
import dev.sethan8r.grammar.app.data.local.user.entity.UserMicrotopicProgress
import kotlinx.coroutines.flow.Flow

/**
 * Прогресс обучения в user.db. Запись через `@Upsert` (update-or-insert, без REPLACE-удаления строки).
 * Базовый набор — расширяется по мере появления экранов теории/упражнений.
 */
@Dao
interface ProgressDao {

    @Upsert
    suspend fun upsertCardProgress(progress: UserCardProgress)

    @Query("SELECT * FROM user_card_progress WHERE cardId = :cardId")
    suspend fun getCardProgress(cardId: Int): UserCardProgress?

    /** ID карточек, помеченных пройденными — для слота «умное задание» (показывается только им). */
    @Query("SELECT cardId FROM user_card_progress WHERE isCompleted = 1")
    fun getCompletedCardIds(): Flow<List<Int>>

    @Upsert
    suspend fun upsertMicrotopicProgress(progress: UserMicrotopicProgress)

    @Query("SELECT * FROM user_microtopic_progress")
    fun getAllMicrotopicProgress(): Flow<List<UserMicrotopicProgress>>

    @Upsert
    suspend fun upsertHardcodeStats(stats: UserCardHardcodeStats)

    @Query("SELECT * FROM user_card_hardcode_stats WHERE cardId = :cardId")
    suspend fun getHardcodeStats(cardId: Int): UserCardHardcodeStats?

    @Upsert
    suspend fun upsertAiExerciseStats(stats: UserAiExerciseStats)

    @Query("SELECT * FROM user_ai_exercise_stats WHERE exerciseId = :exerciseId")
    suspend fun getAiExerciseStats(exerciseId: String): UserAiExerciseStats?

    /** Избранное: UNIQUE на exerciseId, IGNORE — повторное добавление молча игнорируется. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteAiExercise)

    @Query("DELETE FROM favorite_ai_exercise WHERE exerciseId = :exerciseId")
    suspend fun removeFavorite(exerciseId: String)

    @Query("SELECT * FROM favorite_ai_exercise ORDER BY id DESC")
    fun getFavorites(): Flow<List<FavoriteAiExercise>>
}