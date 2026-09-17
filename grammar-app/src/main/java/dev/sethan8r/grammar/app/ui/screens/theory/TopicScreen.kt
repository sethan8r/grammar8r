package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.core.model.theory.MicrotopicSummary
import dev.sethan8r.grammar.app.ui.components.scaffold.BackTopBar
import dev.sethan8r.grammar.app.ui.components.scaffold.PinnedHeader
import dev.sethan8r.grammar.app.ui.components.theory.MicrotopicRow
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.util.scrollBottomInset
import dev.sethan8r.grammar.app.ui.util.statusBarTopInset

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

    Box(modifier = Modifier.fillMaxSize()) {
        // Список проезжает под шапкой, поэтому в покое держим его под ней отступом.
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            contentPadding = PaddingValues(
                top = statusBarTopInset() + Dimens.topBarHeight + Dimens.spaceMedium,
                bottom = scrollBottomInset(),
            ),
        ) {
            items(items = uiState.microtopics, key = { it.id }) { microtopic ->
                MicrotopicRow(
                    microtopic = microtopic,
                    onMicrotopicClick = onMicrotopicClick,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.cornerCard))
                        .background(CardBackground),
                )
            }
        }

        PinnedHeader {
            BackTopBar(
                title = uiState.title,
                onBack = onBack,
                modifier = Modifier.height(Dimens.topBarHeight),
            )
        }
    }
}
