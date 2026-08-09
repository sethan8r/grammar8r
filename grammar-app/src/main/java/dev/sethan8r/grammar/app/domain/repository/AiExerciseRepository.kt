package dev.sethan8r.grammar.app.domain.repository

import dev.sethan8r.grammar.app.domain.model.common.ApiResult
import dev.sethan8r.grammar.app.domain.model.exercise.ClarificationAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.ClarificationTurn
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseEvaluation
import dev.sethan8r.grammar.app.domain.model.exercise.GeneratedExercise

/**
 * AI-прокси через наш сервер: генерация задания, оценка ответа, уточнение по теории
 * (см. phase4_server.md → «AI-прокси»). Промты и OpenAI-ключ — только на сервере; клиент шлёт
 * сырые данные (exerciseId, слова, theorySummary) и получает готовый текст.
 *
 * Лимит AI-запросов списывается на [generate] и [clarify], НЕ на [evaluate]. Заглушка
 * ([dev.sethan8r.grammar.app.data.repository.fake.FakeAiExerciseRepository]) возвращает мок-данные;
 * реальная реализация — Фаза 4.
 */
interface AiExerciseRepository {

    /** Сгенерировать задание. `words` клиент собирает по WordSource упражнения, `cardTheory` = theorySummary. */
    suspend fun generate(
        exerciseId: String,
        words: List<String>,
        cardTheory: String?,
    ): ApiResult<GeneratedExercise>

    /** Оценить ответ пользователя на ранее сгенерированное задание (`taskText` хранил клиент). */
    suspend fun evaluate(
        exerciseId: String,
        taskText: String,
        userAnswer: String,
    ): ApiResult<ExerciseEvaluation>

    /**
     * Уточнение «Не совсем понял» по карточке теории. [history] — уже состоявшиеся обмены той же
     * ветки (пустая на первом вопросе): тред задаёт контекст, без него короткое уточнение теряет
     * смысл, а AI повторяет прежний ответ.
     */
    suspend fun clarify(
        cardId: Int,
        cardTheory: String,
        userQuestion: String,
        history: List<ClarificationTurn>,
    ): ApiResult<ClarificationAnswer>
}