package dev.sethan8r.grammar.app.domain.model.progress

/**
 * Итог записи прохождения карточки: к какой микротеме она относится и стала ли микротема полностью
 * пройденной этим прохождением. По [microtopicCompleted] движок решает, показать ли экран сводки.
 */
data class CardCompletion(
    val cardId: Int,
    val microtopicId: Int,
    val microtopicCompleted: Boolean,
)

/**
 * Данные экрана сводки по завершённой микротеме. Хардкод-часть — «верно [hardcodedCorrect] из
 * [hardcodedTotal]». AI-часть ([aiAttempts], [aiAccuracyPercent]) появится в Фазе 3 (умные задания);
 * сейчас приходит пустой (0 / null) и на экране не показывается.
 */
data class MicrotopicCompletionSummary(
    val microtopicTitle: String,
    val hardcodedCorrect: Int,
    val hardcodedTotal: Int,
    val aiAttempts: Int,
    val aiAccuracyPercent: Int?,
)