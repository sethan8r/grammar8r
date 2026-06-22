package dev.sethan8r.grammar.app.domain.model.progress

/**
 * Итог записи прохождения карточки: к какой микротеме она относится и была ли это ПОСЛЕДНЯЯ карточка
 * микротемы (по порядку). По [isLastCard] движок решает: показать экран сводки (последняя) или
 * пролистать на следующую карточку. Намеренно НЕ «вся микротема пройдена» — иначе при повторном
 * прохождении уже пройденной микротемы сводка появлялась бы уже после первой карточки.
 */
data class CardCompletion(
    val cardId: Int,
    val microtopicId: Int,
    val isLastCard: Boolean,
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