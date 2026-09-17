package dev.sethan8r.grammar.shared

import kotlinx.serialization.Serializable

/**
 * Персональные данные подписки — ответ `GET /subscription` (см. phase4_server.md → «Подписка»).
 * Источник для [dev.sethan8r.grammar.core.model.Entitlements] на клиенте (маппинг на границе).
 *
 * `expires` — ISO-8601 строка или null (бессрочно, ручная выдача).
 * `microtopicsDailyLimit` — null = безлимит (Tier1/Tier2).
 *
 * ⚠️ Лимита «слов в промте» здесь НЕТ — выборка слов фиксирована для всех тиров (отменён 12.06.2026).
 */
@Serializable
data class SubscriptionDto(
    val tier: SubscriptionTier,
    val expires: String? = null,
    val aiRequestsToday: Int,
    val aiDailyLimit: Int,
    val microtopicsToday: Int,
    val microtopicsDailyLimit: Int? = null,
    val streakCurrent: Int,
    val streakBonusRequests: Int,
)