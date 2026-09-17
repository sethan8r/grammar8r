package dev.sethan8r.grammar.app.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.core.repository.EntitlementsProvider
import dev.sethan8r.grammar.shared.SubscriptionTier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** UI-state экрана лимитов: тир и расход дневных AI-запросов. */
data class AiLimitUiState(
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val dailyLimit: Int = 0,
    val spentToday: Int = 0,
    val requestsLeft: Int = 0,
    val isUnlimited: Boolean = false,
) {
    /** Предлагать апгрейд есть смысл только тем, кому лимит вообще мешает. */
    val canUpgrade: Boolean get() = !isUnlimited && tier != SubscriptionTier.TIER2
}

@HiltViewModel
class AiLimitViewModel @Inject constructor(
    entitlementsProvider: EntitlementsProvider,
) : ViewModel() {

    val uiState: StateFlow<AiLimitUiState> = entitlementsProvider.entitlements
        .map { entitlements ->
            AiLimitUiState(
                tier = entitlements.tier,
                dailyLimit = entitlements.aiDailyLimit,
                spentToday = entitlements.aiRequestsToday,
                requestsLeft = entitlements.aiRequestsLeft,
                isUnlimited = entitlements.isAiUnlimited,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AiLimitUiState(),
        )
}
