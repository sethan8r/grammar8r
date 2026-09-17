package dev.sethan8r.grammar.core.repository

import dev.sethan8r.grammar.core.model.exercise.ExerciseSession

/**
 * Доступ к упражнениям карточки (content.db, read-only). Реализация читает индекс упражнений
 * (`CardExerciseIndex`, порядок по `orderInCard`), тянет каждое упражнение из таблицы своего типа и
 * разбирает сырой JSON в типизированные модели. Нереализованные движком типы отдаёт как
 * [dev.sethan8r.grammar.core.model.exercise.Exercise.Unsupported].
 */
interface ExerciseRepository {

    /** Сессия упражнений карточки: мета (заголовок, краткое резюме) + упражнения по порядку. */
    suspend fun getSession(cardId: Int): ExerciseSession
}