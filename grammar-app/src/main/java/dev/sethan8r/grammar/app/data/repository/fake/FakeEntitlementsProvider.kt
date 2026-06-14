package dev.sethan8r.grammar.app.data.repository.fake

import dev.sethan8r.grammar.app.di.DebugBuild
import dev.sethan8r.grammar.app.domain.model.Entitlements
import dev.sethan8r.grammar.app.domain.repository.EntitlementsProvider
import dev.sethan8r.grammar.shared.SubscriptionTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заглушка прав/лимитов (Фаза 1). dev/prod-разница — через [DebugBuild] (BuildConfig.DEBUG,
 * проброшен через DI, не хардкод-комментарий):
 * - debug → топ-тир [SubscriptionTier.ADMIN]: всё открыто, лимиты безграничны (удобно гонять теорию);
 * - release → [SubscriptionTier.FREE] с дефолтными лимитами.
 *
 * [refresh] — no-op (нет сервера). Реальная реализация (`GET /subscription` + кэш) — Фаза 4.
 * `isDebug` инжектится как Boolean → провайдер тестируем без Android.
 */
@Singleton
class FakeEntitlementsProvider @Inject constructor(
    @DebugBuild isDebug: Boolean,
) : EntitlementsProvider {

    private val _entitlements = MutableStateFlow(
        if (isDebug) adminEntitlements() else freeEntitlements()
    )
    override val entitlements = _entitlements.asStateFlow()

    override suspend fun refresh() {
        // Заглушка: сервера нет, перечитывать нечего.
    }

    private fun adminEntitlements() = Entitlements(
        tier = SubscriptionTier.ADMIN,
        expiresAt = null,
        aiRequestsToday = 0,
        aiDailyLimit = Int.MAX_VALUE,
        microtopicsToday = 0,
        microtopicsDailyLimit = null,
        streakCurrent = 0,
        streakBonusRequests = 0,
    )

    private fun freeEntitlements() = Entitlements(
        tier = SubscriptionTier.FREE,
        expiresAt = null,
        aiRequestsToday = 0,
        aiDailyLimit = FREE_AI_PER_DAY,
        microtopicsToday = 0,
        microtopicsDailyLimit = FREE_MICROTOPICS_PER_DAY,
        streakCurrent = 0,
        streakBonusRequests = 0,
    )

    private companion object {
        // Дефолтные Free-лимиты (subscription.md). На сервере лежат в subscription_limits и
        // приходят с `GET /subscription`; здесь — разумная заглушка до появления сервера.
        const val FREE_AI_PER_DAY = 3
        const val FREE_MICROTOPICS_PER_DAY = 3
    }
}