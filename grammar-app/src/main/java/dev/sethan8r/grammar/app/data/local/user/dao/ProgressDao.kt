package dev.sethan8r.grammar.app.data.local.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.sethan8r.grammar.app.data.local.user.entity.FavoriteAiExercise
import dev.sethan8r.grammar.app.data.local.user.entity.UserAiExerciseStats
import dev.sethan8r.grammar.app.data.local.user.entity.UserCardProgress
import dev.sethan8r.grammar.app.data.local.user.entity.UserExerciseResult
import dev.sethan8r.grammar.app.data.local.user.entity.UserMicrotopicProgress
import dev.sethan8r.grammar.app.domain.model.exercise.HardcodedExerciseType
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

    /**
     * Результат упражнения. IGNORE — строку создаёт ПЕРВЫЙ ответ, повторные заходы её не трогают
     * (анти-чит: перепройти упражнение и переписать статистику нельзя).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun recordExerciseResult(result: UserExerciseResult)

    /**
     * Подъём результата «неверно → верно»: верный ответ со второй попытки в том же заходе.
     * Только в одну сторону — понизить результат нельзя. Право на вызов держит ViewModel сессии
     * (обновляет лишь те строки, которые создала сама).
     */
    @Query(
        "UPDATE user_exercise_results SET correctFirstTry = 1 " +
            "WHERE cardId = :cardId AND exerciseType = :type AND exerciseId = :exerciseId",
    )
    suspend fun markExerciseCorrect(cardId: Int, type: HardcodedExerciseType, exerciseId: Int)

    /** Результаты упражнений карточки — для зелёного ID (пройдено) текущей сессии. */
    @Query("SELECT * FROM user_exercise_results WHERE cardId = :cardId")
    suspend fun getExerciseResults(cardId: Int): List<UserExerciseResult>

    /** Результаты по карточкам микротемы — для сводки «верно X из N» на экране завершения. */
    @Query("SELECT * FROM user_exercise_results WHERE cardId IN (:cardIds)")
    suspend fun getExerciseResultsForCards(cardIds: List<Int>): List<UserExerciseResult>

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