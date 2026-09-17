package dev.sethan8r.grammar.app.domain.model.subscription

import dev.sethan8r.grammar.shared.SubscriptionTier
import java.time.Instant

/**
 * Доменное представление прав/лимитов пользователя — то, что отдаёт
 * [dev.sethan8r.grammar.app.domain.repository.EntitlementsProvider] (единый источник тира/лимитов).
 *
 * Маппится из [dev.sethan8r.grammar.shared.SubscriptionDto] на границе data-слоя (разные классы:
 * DTO — транспорт, это — домен). `microtopicsDailyLimit == null` — безлимит (Tier1/Tier2/Admin).
 * `expiresAt == null` — бессрочно. Даты — `java.time` (minSdk 30, нативно).
 */
data class Entitlements(
    val tier: SubscriptionTier,
    val expiresAt: Instant?,
    val aiRequestsToday: Int,
    val aiDailyLimit: Int,
    val microtopicsToday: Int,
    val microtopicsDailyLimit: Int?,
    val streakCurrent: Int,
    val streakBonusRequests: Int,
) {
    /** Остался ли дневной лимит AI-запросов (упражнение/уточнение/практика). */
    val hasAiQuota: Boolean get() = aiRequestsToday < aiDailyLimit

    /** Сколько AI-запросов осталось сегодня (не отрицательное). */
    val aiRequestsLeft: Int get() = (aiDailyLimit - aiRequestsToday).coerceAtLeast(0)

    /**
     * Безлимитный AI (служебные тиры). Счётчик остатка тогда рисует «∞»: показывать
     * `Int.MAX_VALUE` числом бессмысленно.
     */
    val isAiUnlimited: Boolean get() = aiDailyLimit == Int.MAX_VALUE
}