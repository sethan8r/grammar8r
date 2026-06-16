package dev.sethan8r.grammar.app.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Полноэкранные роуты движка упражнений (вне белого списка навбара → без нижней панели).
 * Аргументы типизированы (Navigation Compose 2.8, `toRoute`).
 */

/** Сессия упражнений одной карточки. */
@Serializable
data class ExerciseSessionRoute(val cardId: Int)

/** Экран сводки по завершённой микротеме (показывается, когда пройдены все её карточки). */
@Serializable
data class MicrotopicSummaryRoute(val microtopicId: Int)