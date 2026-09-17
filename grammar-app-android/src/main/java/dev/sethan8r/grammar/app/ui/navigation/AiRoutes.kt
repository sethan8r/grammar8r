package dev.sethan8r.grammar.app.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Полноэкранные роуты AI-части. Аргументы типизированы (Navigation Compose 2.8, `toRoute`).
 *
 * [ClarifyRoute] — уточнение по карточке теории («Не совсем понял»).
 * [AiLimitRoute] — что за подписка и сколько запросов к ИИ осталось; открывается по счётчику
 * остатка с любого AI-экрана.
 */
@Serializable
data class ClarifyRoute(val cardId: Int)

@Serializable
data object AiLimitRoute
