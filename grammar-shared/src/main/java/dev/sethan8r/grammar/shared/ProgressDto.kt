package dev.sethan8r.grammar.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO синхронизации прогресса — контракт `POST /progress/sync` и `GET /progress`
 * (см. phase4_server.md → «Синхронизация прогресса»).
 *
 * Офлайн-буфер событий копится на клиенте и улетает пачкой при появлении сети; снимок (`GET /progress`)
 * используется при переустановке/смене телефона. Контракт минимальный — поля dailyStats/streakData
 * добавятся, когда появится их синк.
 */

/** Тип события прогресса — контрактный enum (`user_progress.event_type` на сервере). */
@Serializable
enum class ProgressEventType {
    @SerialName("card_completed") CARD_COMPLETED,
    @SerialName("exercise_done")  EXERCISE_DONE,
}

@Serializable
data class ProgressEventDto(
    val type: ProgressEventType,
    val entityId: Int,
    val score: Int? = null,
    val timestamp: String,
)

@Serializable
data class ProgressSyncRequest(val events: List<ProgressEventDto>)

@Serializable
data class ProgressSnapshotDto(
    val completedCardIds: List<Int> = emptyList(),
)