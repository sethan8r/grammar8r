package dev.sethan8r.grammar.app.data.repository.fake

import dev.sethan8r.grammar.app.domain.model.common.ApiResult
import dev.sethan8r.grammar.app.domain.model.exercise.ClarificationAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.ClarificationTurn
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseEvaluation
import dev.sethan8r.grammar.app.domain.model.exercise.GeneratedExercise
import dev.sethan8r.grammar.app.domain.repository.AiExerciseRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заглушка AI-прокси (Фаза 1): возвращает фиксированные мок-данные, не ходит на сервер и не
 * тратит лимит. Позволяет прогнать UI AI-блока и экрана уточнения до Фазы 4. Реальная реализация
 * (HTTP → наш сервер → OpenAI) подменит её через тот же интерфейс.
 *
 * TODO(Фаза 4, S6/S7): заменить на HTTP-реализацию — `POST /ai/exercise`, `POST /ai/clarification`
 *  (тред уходит в теле запроса, лимит сервер считает и возвращает сам). См. phase4_server.md.
 */
@Singleton
class FakeAiExerciseRepository @Inject constructor() : AiExerciseRepository {

    override suspend fun generate(
        exerciseId: String,
        words: List<String>,
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
        history: List<ClarificationTurn>,
    ): ApiResult<ClarificationAnswer> {
        // Пауза «модель думает» — чтобы UI ожидания можно было увидеть до появления сервера.
        delay(CLARIFY_DELAY_MS)
        return ApiResult.Success(
            ClarificationAnswer(CLARIFY_ANSWERS[history.size % CLARIFY_ANSWERS.size])
        )
    }

    private companion object {
        const val CLARIFY_DELAY_MS = 3_500L

        /** Ответы-заглушки идут по кругу — по ним видно, что следующий вопрос даёт другой текст. */
        val CLARIFY_ANSWERS = listOf(
            "Will просто указывает на то, что действие произойдёт в будущем: I will call you " +
                "tomorrow. А will be — это будущая форма глагола to be, она нужна, когда после " +
                "неё идёт прилагательное или существительное, а не смысловой глагол: She will be " +
                "ready by six. Проверить просто: если по-русски получается «будет + делать» — " +
                "хватит одного will, а если «будет + какой-то / кто-то» — нужен will be. " +
                "Русский здесь сбивает с толку, потому что в нём «буду» одинаково стоит и перед " +
                "глаголом, и перед прилагательным. В английском же be — это отдельный глагол, и " +
                "он никуда не исчезает, просто получает перед собой will.",
            "Зайдём с другой стороны. Will — это не самостоятельное слово со смыслом «буду», а " +
                "маркер, который ставится перед любым глаголом в начальной форме. Если глагол " +
                "смысловой, получается will go, will work, will call. Если глагол — be, " +
                "получается ровно то же самое: will be. Никакого особого правила для will be нет, " +
                "это обычный will плюс обычный be. Например: The shop will be closed tomorrow. " +
                "Как только вы перестанете считать will be отдельной конструкцией, путаница " +
                "уйдёт сама.",
        )
    }
}