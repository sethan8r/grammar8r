package dev.sethan8r.grammar.app.data.repository

import android.util.Log
import dev.sethan8r.grammar.app.data.local.content.dao.TheoryDao
import dev.sethan8r.grammar.app.data.local.user.dao.ProgressDao
import dev.sethan8r.grammar.app.data.local.user.entity.UserCardProgress
import dev.sethan8r.grammar.app.data.local.user.entity.UserExerciseResult
import dev.sethan8r.grammar.app.data.local.user.entity.UserMicrotopicProgress
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseRef
import dev.sethan8r.grammar.app.domain.model.exercise.HardcodedExerciseType
import dev.sethan8r.grammar.app.domain.model.progress.CardCompletion
import dev.sethan8r.grammar.app.domain.model.progress.MicrotopicCompletionSummary
import dev.sethan8r.grammar.app.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Запись прогресса в user.db + склейка с деревом из content.db (между БД нет JOIN/FK — собираем в коде).
 * Анти-чит: результат упражнения write-once (DAO `INSERT OR IGNORE`); флаги завершения не снимаются.
 *
 * Источник правды по пройденности/счёту — пер-упражнённые `UserExerciseResult` (зелёный ID и сводка
 * считаются из них). `UserCardProgress.isCompleted` — только флаг «вся карточка пройдена».
 */
class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao,
    private val theoryDao: TheoryDao,
) : ProgressRepository {

    override suspend fun recordExerciseResult(
        cardId: Int,
        type: HardcodedExerciseType,
        exerciseId: Int,
        correctFirstTry: Boolean,
    ) {
        progressDao.recordExerciseResult(
            UserExerciseResult(
                cardId = cardId,
                exerciseType = type,
                exerciseId = exerciseId,
                correctFirstTry = correctFirstTry,
            ),
        )
    }

    override suspend fun getPassedExercises(cardId: Int): Set<ExerciseRef> =
        progressDao.getExerciseResults(cardId)
            .map { ExerciseRef(it.exerciseType, it.exerciseId) }
            .toSet()

    override suspend fun completeCard(cardId: Int): CardCompletion {
        progressDao.upsertCardProgress(UserCardProgress(cardId = cardId, isCompleted = true))

        val microtopicId = theoryDao.getCard(cardId)?.microtopicId
        if (microtopicId == null) {
            // Карточка пройдена, но в content.db её нет — битый/разъехавшийся контент.
            // TODO(metrics): отправлять это состояние в метрику (Analytics), когда она появится, —
            //  сигнал рассинхрона контента после обновления с сервера.
            Log.w(TAG, "completeCard: card $cardId отсутствует в content.db (рассинхрон контента)")
            return CardCompletion(cardId = cardId, microtopicId = null, isLastCard = false)
        }

        val cardIds = theoryDao.getCards(microtopicId).first().map { it.id }
        val completedIds = progressDao.getCompletedCardIds().first().toSet()
        // Флаг «вся микротема пройдена» (зелёный сегмент) — когда пройдены ВСЕ карточки (идемпотентно на повторе).
        if (cardIds.isNotEmpty() && cardIds.all { it in completedIds }) {
            progressDao.upsertMicrotopicProgress(
                UserMicrotopicProgress(microtopicId = microtopicId, isCompleted = true),
            )
        }
        // Сводку показываем по завершении ПОСЛЕДНЕЙ карточки (по порядку `order`), а не «всё пройдено» —
        // иначе на повторном прохождении сводка вылезала бы уже после первой карточки (cardIds упорядочены).
        val isLastCard = cardIds.lastOrNull() == cardId
        return CardCompletion(cardId = cardId, microtopicId = microtopicId, isLastCard = isLastCard)
    }

    override suspend fun getMicrotopicSummary(microtopicId: Int): MicrotopicCompletionSummary {
        val title = theoryDao.observeMicrotopic(microtopicId).first()?.title.orEmpty()
        val cardIds = theoryDao.getCards(microtopicId).first().map { it.id }
        val results = progressDao.getExerciseResultsForCards(cardIds)
        return MicrotopicCompletionSummary(
            microtopicTitle = title,
            hardcodedCorrect = results.count { it.correctFirstTry },
            hardcodedTotal = results.size,
            // AI-сводка — Фаза 3 (умные задания ещё заглушка): данных нет.
            aiAttempts = 0,
            aiAccuracyPercent = null,
        )
    }

    override suspend fun isCardCompleted(cardId: Int): Boolean =
        progressDao.getCardProgress(cardId)?.isCompleted == true

    private companion object {
        const val TAG = "ProgressRepository"
    }
}