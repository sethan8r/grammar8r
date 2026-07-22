package dev.sethan8r.grammar.app.domain.repository

import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseRef
import dev.sethan8r.grammar.app.domain.model.exercise.HardcodedExerciseType
import dev.sethan8r.grammar.app.domain.model.progress.CardCompletion
import dev.sethan8r.grammar.app.domain.model.progress.MicrotopicCompletionSummary

/**
 * ЕДИНАЯ точка записи прогресса прохождения: все отметки идут только сюда — разрозненные
 * `dao.update()` из ViewModel запрещены. Несёт логику (две БД, проверка
 * «все карточки пройдены», write-once результатов), поэтому это репозиторий, а не анемичный прокси.
 *
 * Анти-чит (CLAUDE → «Повторное прохождение»): результат упражнения принадлежит первому заходу —
 * перезаход его не меняет; флаги завершения только выставляются и не снимаются.
 */
interface ProgressRepository {

    /**
     * Результат упражнения в момент первого ответа. Источник правды для зелёного ID и для сводки
     * «верно X из N». Повторный заход строку не перезаписывает.
     */
    suspend fun recordExerciseResult(
        cardId: Int,
        type: HardcodedExerciseType,
        exerciseId: Int,
        correctFirstTry: Boolean,
    )

    /**
     * Поднимает результат до верного — верный ответ со ВТОРОЙ попытки в том же заходе. Работает
     * только на повышение; вызывать имеет право лишь та сессия, которая создала строку.
     */
    suspend fun markExerciseCorrect(cardId: Int, type: HardcodedExerciseType, exerciseId: Int)

    /** Упражнения карточки, по которым уже есть результат (для зелёного ID при заходе). */
    suspend fun getPassedExercises(cardId: Int): Set<ExerciseRef>

    /**
     * Отмечает карточку пройденной; если все карточки микротемы пройдены — отмечает и микротему.
     * Возвращает, к какой микротеме относится карточка и стала ли микротема завершённой.
     */
    suspend fun completeCard(cardId: Int): CardCompletion

    /** Сводка по завершённой микротеме (заголовок + «верно X из N» из результатов + AI-часть Фазы 3). */
    suspend fun getMicrotopicSummary(microtopicId: Int): MicrotopicCompletionSummary

    /** Пройдена ли карточка ранее — по этому флагу полоса прогресса в сессии становится кликабельной. */
    suspend fun isCardCompleted(cardId: Int): Boolean
}