package dev.sethan8r.grammar.app.ui.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.core.model.progress.MicrotopicCompletionSummary
import dev.sethan8r.grammar.app.ui.components.LoadingIndicator
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Экран сводки по завершённой микротеме. Показывается, когда пройдены ВСЕ карточки микротемы:
 * поздравление + краткий итог по заданиям (хардкод — «верно X из N»; AI — Фаза 3). Кнопка «Далее»
 * возвращает в список микротем, наведённый на пройденную (логика навигации — на уровне графа).
 */
@Composable
fun MicrotopicSummaryScreen(
    onContinue: () -> Unit,
    viewModel: MicrotopicSummaryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = state.summary

    when {
        state.isLoading || summary == null -> LoadingIndicator(Modifier.fillMaxSize())
        else -> SummaryContent(summary = summary, onContinue = onContinue)
    }
}

@Composable
private fun SummaryContent(summary: MicrotopicCompletionSummary, onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = CorrectGreen,
            modifier = Modifier.size(Dimens.spaceXXLarge),
        )
        Text(
            text = stringResource(R.string.microtopic_done_title),
            modifier = Modifier.padding(top = Dimens.spaceMedium),
            color = TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = summary.microtopicTitle,
            modifier = Modifier.padding(top = Dimens.spaceTiny),
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        // Хардкод-сводка — только если в микротеме были задания.
        if (summary.hardcodedTotal > 0) {
            StatRow(
                label = stringResource(R.string.microtopic_done_hardcoded),
                value = stringResource(
                    R.string.microtopic_done_correct_of,
                    summary.hardcodedCorrect,
                    summary.hardcodedTotal,
                ),
                modifier = Modifier.padding(top = Dimens.spaceXLarge),
            )
        }
        // AI-сводка (умные задания) — Фаза 3: показываем только когда есть тренировки.
        if (summary.aiAttempts > 0) {
            StatRow(
                label = stringResource(R.string.microtopic_done_ai),
                value = stringResource(
                    R.string.microtopic_done_ai_value,
                    summary.aiAttempts,
                    summary.aiAccuracyPercent ?: 0,
                ),
                modifier = Modifier.padding(top = Dimens.spaceMedium),
            )
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.spaceXXLarge),
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background),
        ) {
            Text(stringResource(R.string.exercise_next))
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground)
            .padding(Dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
    ) {
        Text(text = label, color = TextSecondary, fontSize = 14.sp)
        Text(text = value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}