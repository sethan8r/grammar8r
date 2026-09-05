package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.theory.TheoryListItem
import dev.sethan8r.grammar.app.ui.components.CenteredHint
import dev.sethan8r.grammar.app.ui.components.DualTitle
import dev.sethan8r.grammar.app.ui.components.InfoButton
import dev.sethan8r.grammar.app.ui.components.feedback.LocalTabSnackbarController
import dev.sethan8r.grammar.app.ui.components.search.SearchGroupCard
import dev.sethan8r.grammar.app.ui.components.search.SearchIdleHint
import dev.sethan8r.grammar.app.ui.components.search.SearchNoResults
import dev.sethan8r.grammar.app.ui.components.search.TheorySearchBar
import dev.sethan8r.grammar.app.ui.components.theory.TopicCardBody
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.util.floatingBarBottomInset
import dev.sethan8r.grammar.app.ui.util.statusBarTopInset

/**
 * Вкладка «Учить» — список разделов и тем теории с прогрессом по микротемам. Раздел сворачивается;
 * описания скрыты под кнопкой «i» ([InfoButton]) — по тапу всплывают нижним снекбаром. Только UI:
 * данные из [TheoryViewModel].
 */
@Composable
fun TheoryScreen(
    onTopicClick: (Int) -> Unit,
    onMicrotopicClick: (Int) -> Unit,
    resetSearch: Boolean = false,
    onResetSearchConsumed: () -> Unit = {},
    viewModel: TheoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Снекбар вкладок общий (висит над капсулой навигации в MainScreen) — берём из CompositionLocal.
    val snackbar = LocalTabSnackbarController.current

    // Вкладку открыли нижней панелью — поиск закрываем. Возврат «назад» из микротемы флага не
    // ставит, поэтому там запрос и выдача остаются как были.
    LaunchedEffect(resetSearch) {
        if (resetSearch) {
            viewModel.onSearchClose()
            onResetSearchConsumed()
        }
    }

    // Поиск — режим вкладки, а не отдельный роут, поэтому системная «назад» сама его не свернёт.
    // Перехват включён ТОЛЬКО при открытом поиске: закрытый оставляет кнопку вкладке.
    BackHandler(enabled = uiState.isSearchOpen) { viewModel.onSearchClose() }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> CenteredHint(stringResource(R.string.theory_loading), Modifier.fillMaxSize())
            uiState.items.isEmpty() -> CenteredHint(stringResource(R.string.theory_empty), Modifier.fillMaxSize())
            else -> TheoryList(
                uiState = uiState,
                onTopicClick = onTopicClick,
                onMicrotopicClick = onMicrotopicClick,
                onShowInfo = { snackbar?.show(it, Durations.infoSnackbarMs) },
                onSearchOpen = viewModel::onSearchOpen,
                onSearchClear = viewModel::onQueryClear,
                onSearchClose = viewModel::onSearchClose,
                onQueryChange = viewModel::onQueryChange,
                onFocusConsumed = viewModel::onSearchFocusConsumed,
            )
        }
    }
}

@Composable
private fun TheoryList(
    uiState: TheoryUiState,
    onTopicClick: (Int) -> Unit,
    onMicrotopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
    onSearchOpen: () -> Unit,
    onSearchClear: () -> Unit,
    onSearchClose: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFocusConsumed: () -> Unit,
) {
    // Своё состояние скролла на каждый режим: закрыв поиск, пользователь возвращается туда,
    // где читал дерево.
    val treeState = rememberLazyListState()
    val resultsState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarTopInset()),
    ) {
        // Шапка закреплена: в режиме поиска строка ввода и выход из него нужны под рукой
        // независимо от того, куда пользователь пролистал выдачу.
        // Слева отступ меньше общего: его добирает зона нажатия стрелки «назад», и глиф встаёт
        // ровно там же, где в шапке подэкранов ([BackTopBar]).
        TheorySearchBar(
            isOpen = uiState.isSearchOpen,
            query = uiState.query,
            requestFocus = uiState.requestSearchFocus,
            onQueryChange = onQueryChange,
            onOpen = onSearchOpen,
            onClear = onSearchClear,
            onClose = onSearchClose,
            onFocusConsumed = onFocusConsumed,
            modifier = Modifier.padding(
                start = Dimens.spaceSmall,
                end = Dimens.screenPadding,
                top = Dimens.spaceSmall,
                bottom = Dimens.spaceSmall,
            ),
        )

        // Тело сменяется со сдвигом вниз: уходит дерево — приходит выдача, и наоборот.
        AnimatedContent(
            targetState = uiState.isSearchOpen,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                (fadeIn(tween(Durations.searchBodySwapMs)) +
                    slideInVertically(tween(Durations.searchBodySwapMs)) { it / SEARCH_BODY_SLIDE })
                    .togetherWith(
                        fadeOut(tween(Durations.searchBarSwapMs)) +
                            slideOutVertically(tween(Durations.searchBarSwapMs)) { it / SEARCH_BODY_SLIDE }
                    )
            },
            label = "theory_body",
        ) { searchOpen ->
            LazyColumn(
                state = if (searchOpen) resultsState else treeState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.screenPadding),
                // Низ: клиренс под плавающей капсулой навигации (она парит поверх, места не резервирует).
                contentPadding = PaddingValues(bottom = floatingBarBottomInset()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            ) {
                if (searchOpen) {
                    searchContent(uiState.searchContent, onTopicClick, onMicrotopicClick, onShowInfo)
                } else {
                    items(items = uiState.items, key = { it.itemKey() }) { item ->
                        when (item) {
                            is TheoryListItem.TopicItem -> TopicCardBody(
                                topic = item.topic,
                                onTopicClick = onTopicClick,
                                onShowInfo = onShowInfo,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Dimens.cornerCard))
                                    .background(CardBackground),
                            )
                            is TheoryListItem.SectionItem -> SectionGroup(item, onTopicClick, onShowInfo)
                        }
                    }
                }
            }
        }
    }
}

/** Доля высоты тела, на которую оно сдвигается при смене дерева и выдачи. */
private const val SEARCH_BODY_SLIDE = 6

/** Тело вкладки в режиме поиска: подсказка, «ничего не найдено» или группы результатов. */
private fun LazyListScope.searchContent(
    content: TheorySearchContent,
    onTopicClick: (Int) -> Unit,
    onMicrotopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
) {
    when (content) {
        is TheorySearchContent.Idle -> item(key = "search_idle") { SearchIdleHint() }
        is TheorySearchContent.NoResults -> item(key = "search_empty") { SearchNoResults() }
        is TheorySearchContent.Results -> items(
            items = content.groups,
            key = { "group_${it.topic.id}" },
        ) { group ->
            SearchGroupCard(
                group = group,
                onTopicClick = onTopicClick,
                onMicrotopicClick = onMicrotopicClick,
                onShowInfo = onShowInfo,
            )
        }
    }
}

private fun TheoryListItem.itemKey(): String = when (this) {
    is TheoryListItem.TopicItem -> "topic_${topic.id}"
    is TheoryListItem.SectionItem -> "section_$id"
}

/**
 * Раздел в едином фрейме (как раскрытие категории в словаре Words8r): кликабельная шапка со
 * стрелкой ▾/▸ и кнопкой «i», а раскрытые темы лежат внутри того же фрейма, разделённые линиями.
 */
@Composable
private fun SectionGroup(
    section: TheoryListItem.SectionItem,
    onTopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
) {
    var expanded by rememberSaveable(section.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(Dimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val rotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                label = "section_arrow",
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.rotate(rotation),
            )
            DualTitle(
                title = section.title,
                modifier = Modifier.weight(1f).padding(start = Dimens.spaceSmall),
                primarySize = 18.sp,
            )
            section.description?.let { description -> InfoButton(onClick = { onShowInfo(description) }) }
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                section.topics.forEach { topic ->
                    HorizontalDivider(color = Inactive)
                    TopicCardBody(topic, onTopicClick, onShowInfo)
                }
            }
        }
    }
}
