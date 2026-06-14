package dev.sethan8r.grammar.app.data.repository.fake

import dev.sethan8r.grammar.app.domain.model.ApiResult
import dev.sethan8r.grammar.app.domain.model.ClarificationAnswer
import dev.sethan8r.grammar.app.domain.model.ExerciseEvaluation
import dev.sethan8r.grammar.app.domain.model.GeneratedExercise
import dev.sethan8r.grammar.app.domain.repository.AiExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заглушка AI-прокси (Фаза 1): возвращает фиксированные мок-данные, не ходит на сервер и не
 * тратит лимит. Позволяет прогнать UI AI-блока и экрана уточнения до Фазы 4. Реальная реализация
 * (HTTP → наш сервер → OpenAI) подменит её через тот же интерфейс.
 */
@Singleton
class FakeAiExerciseRepository @Inject constructor() : AiExerciseRepository {

    override suspend fun generate(
        exerciseId: String,
        words: List<String>,
        cardTheory: String?,
    ): ApiResult<GeneratedExercise> = ApiResult.Success(
        GeneratedExercise(
            taskText = "Переведите на английский: «Я каждый день пью кофе по утрам»."
        )
    )

    override suspend fun evaluate(
        exerciseId: String,
        taskText: String,
        userAnswer: String,
    ): ApiResult<ExerciseEvaluation> = ApiResult.Success(
        ExerciseEvaluation(
            score = 90,
            correctedAnswer = "I drink coffee every morning.",
            feedback = "Хорошо! Небольшая неточность в порядке слов — наречие времени обычно в конце."
        )
    )

    override suspend fun clarify(
        cardId: Int,
        cardTheory: String,
        userQuestion: String,
    ): ApiResult<ClarificationAnswer> = ApiResult.Success(
        ClarificationAnswer(
            answer = "Это заглушка ответа AI на уточнение. Реальное объяснение появится в Фазе 3."
        )
    )
}