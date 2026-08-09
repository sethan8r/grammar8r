package dev.sethan8r.grammar.app.ui.screens.subscription

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.components.scaffold.BackTopBar
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Highlight
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.scrollBottomInset
import dev.sethan8r.grammar.shared.SubscriptionTier

/**
 * Подписка и дневные запросы к ИИ. Открывается по счётчику остатка с любого AI-экрана
 * ([dev.sethan8r.grammar.app.ui.components.ai.AiLimitChip]).
 *
 * Покупка подписки — Фаза 4: блок апгрейда пока только рассказывает, кнопка неактивна.
 */
@Composable
fun AiLimitScreen(
    onBack: () -> Unit,
    viewModel: AiLimitViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        BackTopBar(title = stringResource(R.string.ai_limit_title), onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.screenPadding)
                .padding(top = Dimens.spaceSmall, bottom = scrollBottomInset()),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge),
        ) {
            UsageCard(state)
            if (state.canUpgrade) {
                UpgradeCard()
            }
        }
    }
}

@Composable
private fun UsageCard(state: AiLimitUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground)
            .padding(Dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
    ) {
        // Служебные тиры пользователю не называем — для них показываем только сам факт безлимита.
        state.tier.userVisibleName()?.let { tierName ->
            Text(
                text = stringResource(R.string.ai_limit_tier, stringResource(tierName)),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = if (state.isUnlimited) {
                stringResource(R.string.ai_limit_daily_unlimited)
            } else {
                stringResource(R.string.ai_limit_daily, state.dailyLimit)
            },
            color = TextSecondary,
            fontSize = 14.sp,
        )
        Text(
            text = stringResource(R.string.ai_limit_spent, state.spentToday),
            color = TextSecondary,
            fontSize = 14.sp,
        )
        if (!state.isUnlimited) {
            Text(
                text = stringResource(R.string.ai_limit_left, state.requestsLeft),
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun UpgradeCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .border(Dimens.outlineWidth, Highlight, RoundedCornerShape(Dimens.cornerCard))
            .padding(Dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
    ) {
        Text(
            text = stringResource(R.string.ai_limit_upgrade_pitch),
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Highlight,
                contentColor = Background,
            ),
        ) {
            Text(text = stringResource(R.string.ai_limit_upgrade_action))
        }
    }
}

/** Название тира для пользователя; служебные тиры (тестер/админ) названия не имеют. */
@StringRes
private fun SubscriptionTier.userVisibleName(): Int? = when (this) {
    SubscriptionTier.FREE -> R.string.tier_free
    SubscriptionTier.TIER1 -> R.string.tier_1
    SubscriptionTier.TIER2 -> R.string.tier_2
    SubscriptionTier.TESTER, SubscriptionTier.ADMIN -> null
}
