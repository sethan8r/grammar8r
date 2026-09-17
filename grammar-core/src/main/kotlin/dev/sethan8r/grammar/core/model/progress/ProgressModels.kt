package dev.sethan8r.grammar.core.model.progress

import dev.sethan8r.grammar.shared.ProgressEventType
import java.time.Instant

/**
 * Доменные модели синка прогресса — то, чем оперирует
 * [dev.sethan8r.grammar.core.repository.ProgressSyncRepository].
 *
 * [ProgressEventType] переиспользуется из grammar-shared: набор типов событий одинаков на клиенте
 * и сервере (один и тот же контракт), дублировать его в домене — нарушение «одна сущность один раз».
 * Время — `java.time.Instant`; в DTO сериализуется строкой на границе data-слоя.
 */
data class ProgressEvent(
    val type: ProgressEventType,
    val entityId: Int,
    val score: Int? = null,
    val occurredAt: Instant,
)

/** Снимок прогресса с сервера (восстановление при переустановке/смене телефона). */
data class ProgressSnapshot(val completedCardIds: List<Int>)