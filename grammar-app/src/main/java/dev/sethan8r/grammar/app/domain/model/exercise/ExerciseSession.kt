package dev.sethan8r.grammar.app.domain.model.exercise

/**
 * Набор упражнений одной карточки + её мета для шапки сессии. Собирается
 * [dev.sethan8r.grammar.app.domain.repository.ExerciseRepository] из content.db
 * (карточка + индекс упражнений + сами упражнения по порядку `orderInCard`).
 */
data class ExerciseSession(
    val cardTitle: String,
    /** Название микротемы — в шапке сессии (вместо «X из N»). */
    val microtopicTitle: String,
    /** Краткое резюме правила карточки — показывается по кнопке «?». */
    val theorySummary: String,
    val exercises: List<Exercise>,
)