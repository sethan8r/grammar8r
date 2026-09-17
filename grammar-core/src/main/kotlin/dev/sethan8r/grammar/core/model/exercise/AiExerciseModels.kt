package dev.sethan8r.grammar.core.model.exercise

/**
 * Доменные результаты AI-взаимодействий (генерация задания, оценка ответа, уточнение по теории).
 * Маппятся из соответствующих DTO в grammar-shared на границе data-слоя.
 *
 * Счётчики лимита (aiRequestsToday/aiDailyLimit) из DTO сюда НЕ кладутся — они обновляют
 * [dev.sethan8r.grammar.core.repository.EntitlementsProvider], а не возвращаются в UI как
 * часть контента задания. Здесь — только то, что показывается пользователю.
 */

/** Сгенерированное AI-задание (текст для пользователя). */
data class GeneratedExercise(val taskText: String)

/** Оценка ответа: балл 0–100, исправленный вариант, фидбек. */
data class ExerciseEvaluation(
    val score: Int,
    val correctedAnswer: String,
    val feedback: String,
)

/** Ответ AI на уточняющий вопрос «Не совсем понял» по карточке. */
data class ClarificationAnswer(val answer: String)

/**
 * Один состоявшийся обмен в треде уточнения. Предыдущие обмены уходят в запрос вместе с новым
 * вопросом: без них короткое уточнение («а почему тогда will be?») не с чем связать, а AI
 * повторяет уже сказанное.
 */
data class ClarificationTurn(val question: String, val answer: String)