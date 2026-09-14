package dev.sethan8r.grammar.shared

import kotlinx.serialization.Serializable

/**
 * DTO AI-упражнений и уточнения — контракт `/ai/exercise/generate|evaluate` и `/ai/clarification`
 * (см. phase4_server.md → «AI-прокси»).
 *
 * Промты на сервере (таблица `ai_exercise_prompts`), клиент шлёт только сырые данные:
 * `exerciseId` (= AiExercise.id, по нему сервер находит промт), `words` (клиент собирает сам по
 * WordSource упражнения). Правило карточки уже зашито в серверный промт — клиент его не шлёт.
 */

@Serializable
data class AiExerciseGenerateRequest(
    val exerciseId: String,
    val words: List<String> = emptyList(),
)

/** `taskText` — сгенерированное задание; счётчики лимита — для обновления экрана/`Entitlements`. */
@Serializable
data class AiExerciseGenerateResponse(
    val taskText: String,
    val aiRequestsToday: Int,
    val aiDailyLimit: Int,
)

/** `taskText` — то, что вернул generate (клиент хранил в ViewModel); лимит на evaluate НЕ списывается. */
@Serializable
data class AiExerciseEvaluateRequest(
    val exerciseId: String,
    val taskText: String,
    val userAnswer: String,
)

@Serializable
data class AiExerciseEvaluateResponse(
    val score: Int,
    val correctedAnswer: String,
    val feedback: String,
)

/** Уточнение «Не совсем понял» по карточке теории — отдельный AI-запрос, списывает лимит. */
@Serializable
data class AiClarificationRequest(
    val cardId: Int,
    val cardTheory: String,
    val userQuestion: String,
)

@Serializable
data class AiClarificationResponse(
    val answer: String,
    val aiRequestsToday: Int,
    val aiDailyLimit: Int,
)