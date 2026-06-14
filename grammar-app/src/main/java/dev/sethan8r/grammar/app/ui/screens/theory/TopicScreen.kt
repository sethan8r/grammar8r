package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.domain.model.MicrotopicState
import dev.sethan8r.grammar.app.domain.model.MicrotopicSummary
import dev.sethan8r.grammar.app.ui.components.BackTopBar
import dev.sethan8r.grammar.app.ui.components.DualTitle
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Экран темы — список её микротем. Тап ведёт на листание карточек ([onMicrotopicClick]).
 * Только UI: данные из [TopicViewModel], заголовок темы — оттуда же.
 */
@Composable
fun TopicScreen(
    onMicrotopicClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        BackTopBar(title = uiState.title, onBack = onBack)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            contentPadding = PaddingValues(vertical = Dimens.spaceMedium),
        ) {
            items(items = uiState.microtopics, key = { it.id }) { microtopic ->
                MicrotopicRow(microtopic, onMicrotopicClick)
            }
        }
    }
}

@Composable
private fun MicrotopicRow(microtopic: MicrotopicSummary, onMicrotopicClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground)
            .clickable { onMicrotopicClick(microtopic.id) }
            .padding(Dimens.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
    ) {
        DualTitle(title = microtopic.title, modifier = Modifier.weight(1f))
        if (microtopic.state == MicrotopicState.COMPLETED) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = CorrectGreen,
                modifier = Modifier.size(Dimens.spaceXLarge),
            )
        }
    }
}