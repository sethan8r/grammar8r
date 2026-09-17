package dev.sethan8r.grammar.app.data.repository.fake

import dev.sethan8r.grammar.app.di.DebugBuild
import dev.sethan8r.grammar.core.model.subscription.Entitlements
import dev.sethan8r.grammar.core.repository.EntitlementsProvider
import dev.sethan8r.grammar.shared.SubscriptionTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заглушка прав/лимитов (Фаза 1). Тир debug-сборки задаёт [DEBUG_TIER] — сейчас это
 * [SubscriptionTier.FREE], чтобы на устройстве было видно, как выглядит счётчик остатка запросов и
 * его исчерпание. Поставьте [SubscriptionTier.ADMIN], когда нужно гонять контент без лимитов.
 * Release всегда [SubscriptionTier.FREE].
 *
 * `isDebug` инжектится как Boolean → провайдер тестируем без Android.
 *
 * TODO(Фаза 4, S5): заменить на реальную реализацию — `GET /subscription` (тир, expires,
 *  aiRequestsToday/aiDailyLimit, microtopicsToday/microtopicsDailyLimit, стрик) + локальный кэш;
 *  [refresh] дёргает ручку, [consumeAiRequest] применяет остаток из ответа AI-эндпоинтов
 *  (сервер считает лимит атомарно сам — клиент только отражает). См. phase4_server.md → «Подписка».
 */
@Singleton
class FakeEntitlementsProvider @Inject constructor(
    @DebugBuild isDebug: Boolean,
) : EntitlementsProvider {

    private val _entitlements = MutableStateFlow(
        if (isDebug && DEBUG_TIER == SubscriptionTier.ADMIN) adminEntitlements() else freeEntitlements()
    )
    override val entitlements = _entitlements.asStateFlow()

    override suspend fun refresh() {
        // Заглушка: сервера нет, перечитывать нечего.
    }

    /** Счёт ведём в памяти: до сервера это единственный способ увидеть остаток и его исчерпание. */
    override suspend fun consumeAiRequest() {
        _entitlements.update { current ->
            if (current.isAiUnlimited) current
            else current.copy(aiRequestsToday = (current.aiRequestsToday + 1).coerceAtMost(current.aiDailyLimit))
        }
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
        /** Тир debug-сборки: FREE — видно лимиты и счётчик, ADMIN — всё открыто. */
        val DEBUG_TIER = SubscriptionTier.FREE

        // Дефолтные Free-лимиты (subscription.md). На сервере лежат в subscription_limits и
        // приходят с `GET /subscription`; здесь — разумная заглушка до появления сервера.
        const val FREE_AI_PER_DAY = 3
        const val FREE_MICROTOPICS_PER_DAY = 3
    }
}