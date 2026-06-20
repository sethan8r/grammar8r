package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicState
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicSummary
import dev.sethan8r.grammar.app.ui.components.BackTopBar
import dev.sethan8r.grammar.app.ui.components.DualTitle
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.util.scrollBottomInset

/**
 * Экран темы — список её микротем. Тап ведёт на листание карточек ([onMicrotopicClick]).
 * Только UI: данные из [TopicViewModel], заголовок темы — оттуда же.
 */
@Composable
fun TopicScreen(
    onMicrotopicClick: (Int) -> Unit,
    onBack: () -> Unit,
    focusMicrotopicId: Int? = null,
    onFocusConsumed: () -> Unit = {},
    viewModel: TopicViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Возврат с экрана сводки: проскроллить к только что пройденной микротеме и сбросить запрос.
    LaunchedEffect(focusMicrotopicId, uiState.microtopics) {
        val id = focusMicrotopicId ?: return@LaunchedEffect
        val index = uiState.microtopics.indexOfFirst { it.id == id }
        if (index >= 0) {
            listState.animateScrollToItem(index)
            onFocusConsumed()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BackTopBar(title = uiState.title, onBack = onBack)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            contentPadding = PaddingValues(
                top = Dimens.spaceMedium,
                bottom = scrollBottomInset(),
            ),
        ) {
            items(items = uiState.microtopics, key = { it.id }) { microtopic ->
                MicrotopicRow(microtopic, onMicrotopicClick)
            }
        }
    }
}

/**
 * Строка микротемы. Статус прохождения — вертикальная полоса у левого края (внутри скругления):
 * зелёная для пройденной ([MicrotopicState.COMPLETED]), [Inactive] для непройденной. Галочку не
 * показываем — статус несёт полоса.
 */
@Composable
private fun MicrotopicRow(microtopic: MicrotopicSummary, onMicrotopicClick: (Int) -> Unit) {
    val stripeColor = if (microtopic.state == MicrotopicState.COMPLETED) CorrectGreen else Inactive
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground)
            .clickable { onMicrotopicClick(microtopic.id) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(Dimens.microtopicStripeWidth)
                .fillMaxHeight()
                .background(stripeColor),
        )
        DualTitle(
            title = microtopic.title,
            modifier = Modifier
                .weight(1f)
                .padding(Dimens.cardPadding),
        )
    }
}