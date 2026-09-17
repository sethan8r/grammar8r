package dev.sethan8r.grammar.app.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Полноэкранные роуты читалки теории (вне белого списка навбара → открываются без нижней панели).
 * Аргументы типизированы (Navigation Compose 2.8, `toRoute`) — без конкатенации строк.
 */
@Serializable
data class TopicRoute(val topicId: Int)

@Serializable
data class MicrotopicRoute(val microtopicId: Int)