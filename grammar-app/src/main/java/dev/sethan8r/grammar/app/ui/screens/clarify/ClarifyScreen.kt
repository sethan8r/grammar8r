package dev.sethan8r.grammar.app.ui.screens.clarify

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.components.LoadingIndicator
import dev.sethan8r.grammar.app.ui.components.ai.AiLimitChip
import dev.sethan8r.grammar.app.ui.components.clarify.ClarifyAnswerBubble
import dev.sethan8r.grammar.app.ui.components.clarify.ClarifyFailedBubble
import dev.sethan8r.grammar.app.ui.components.clarify.ClarifyInputBar
import dev.sethan8r.grammar.app.ui.components.clarify.ClarifyQuestionBubble
import dev.sethan8r.grammar.app.ui.components.clarify.ClarifyQuestionOptions
import dev.sethan8r.grammar.app.ui.components.clarify.ClarifyTypingBubble
import dev.sethan8r.grammar.app.ui.components.scaffold.BackTopBar
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.components.titleEn
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.bottomScrim

/**
 * Экран уточнения по карточке теории («Не совсем понял»). Тред «вопрос → ответ»: сначала готовые
 * вопросы карточки, дальше — свободный ввод, который остаётся доступен и после ответа (уточнение
 * продолжает ту же ветку). «Задать другой вопрос» стирает тред и возвращает готовые вопросы.
 *
 * Ответы ИИ в Фазе 1 приходят из заглушки репозитория; экран об этом не знает.
 */
@Composable
fun ClarifyScreen(
    onBack: () -> Unit,
    onOpenAiLimit: () -> Unit,
    viewModel: ClarifyViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Низ держит сам экран: поле ввода встаёт над клавиатурой либо над системной полосой.
            .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars)),
    ) {
        BackTopBar(
            title = state.microtopicTitle.titleEn(),
            onBack = onBack,
            actions = {
                AiLimitChip(
                    requestsLeft = state.aiRequestsLeft,
                    isUnlimited = state.isAiUnlimited,
                    onClick = onOpenAiLimit,
                )
            },
        )

        if (state.isLoading) {
            LoadingIndicator(Modifier.fillMaxSize())
        } else {
            MarkdownText(
                text = state.cardTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceSmall),
                color = TextSecondary,
                fontSize = 13.sp,
            )

            // Лента уходит под футер, футер лежит поверх на затемняющей подложке — как кнопка
            // «Проверить» в сессии упражнений.
            var footerHeight by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current

            Box(modifier = Modifier.weight(1f)) {
                ClarifyThreadList(
                    state = state,
                    onSelectOption = viewModel::ask,
                    bottomPadding = footerHeight,
                )

                Footer(
                    state = state,
                    onInputChange = viewModel::onInputChange,
                    onSend = viewModel::askTyped,
                    onNewBranch = viewModel::startNewBranch,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onGloballyPositioned { footerHeight = with(density) { it.size.height.toDp() } }
                        .bottomScrim(),
                )
            }
        }
    }
}

/** Лента ветки: готовые вопросы на старте, дальше пары «вопрос → ответ» и статус ветки. */
@Composable
private fun ClarifyThreadList(
    state: ClarifyUiState,
    onSelectOption: (String) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // Лента подтягивается к последней реплике, ТОЛЬКО если та не помещается на экране целиком.
    // Иначе выбранный вопрос вставал бы на место готовых и тут же уезжал вверх — лишний скачок.
    LaunchedEffect(state.exchanges.lastOrNull()?.id, state.exchanges.lastOrNull()?.answer) {
        val lastId = state.exchanges.lastOrNull()?.id ?: return@LaunchedEffect
        // Ждём кадр: до перерасчёта раскладки размеры новой реплики ещё не известны.
        withFrameNanos { }
        val layout = listState.layoutInfo
        val item = layout.visibleItemsInfo.firstOrNull { it.key == lastId }
        if (item == null || item.offset + item.size > layout.viewportEndOffset) {
            listState.animateScrollToItem(OPTIONS_ITEM_INDEX + state.exchanges.lastIndex + 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = Dimens.screenPadding,
            end = Dimens.screenPadding,
            top = Dimens.spaceSmall,
            bottom = bottomPadding + Dimens.spaceSmall,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
    ) {
        // Блок готовых вопросов живёт своим элементом: он не удаляется, а схлопывается по высоте,
        // поэтому выбранный вопрос занимает его место без рывка. Сброс ветки разворачивает обратно.
        item(key = OPTIONS_ITEM_KEY) {
            AnimatedVisibility(
                visible = state.exchanges.isEmpty(),
                enter = fadeIn(tween(Durations.clarifyAnswerRevealMs)) +
                    expandVertically(tween(Durations.clarifyAnswerRevealMs)),
                exit = fadeOut(tween(Durations.clarifyOptionsHideMs)) +
                    shrinkVertically(tween(Durations.clarifyOptionsHideMs)),
            ) {
                ClarifyQuestionOptions(
                    options = state.options,
                    enabled = state.canAsk,
                    onSelect = onSelectOption,
                )
            }
        }
        items(state.exchanges, key = { it.id }) { exchange ->
            Column(
                modifier = Modifier.animateItem(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
            ) {
                ClarifyQuestionBubble(exchange.question)
                when {
                    exchange.answer != null -> ClarifyAnswerBubble(exchange.answer)
                    exchange.failed -> ClarifyFailedBubble(stringResource(R.string.clarify_failed))
                    else -> ClarifyTypingBubble()
                }
            }
        }
        if (state.isBranchExhausted || state.isLimitReached) {
            item {
                Text(
                    text = stringResource(
                        if (state.isLimitReached) R.string.clarify_limit_reached
                        else R.string.clarify_branch_exhausted
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSmall),
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Низ экрана: сброс ветки (когда она начата) и поле свободного вопроса — оно доступно всегда. */
@Composable
private fun Footer(
    state: ClarifyUiState,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onNewBranch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
    ) {
        AnimatedVisibility(visible = state.exchanges.isNotEmpty()) {
            OutlinedButton(
                onClick = onNewBranch,
                enabled = !state.isAwaitingAnswer,
                modifier = Modifier.fillMaxWidth(),
                // Кнопка лежит поверх ленты — заливка фоном, чтобы реплики сквозь неё не читались.
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Background),
            ) {
                Text(text = stringResource(R.string.clarify_new_question))
            }
        }
        ClarifyInputBar(
            value = state.input,
            enabled = state.canAsk,
            onValueChange = onInputChange,
            onSend = onSend,
        )
    }
}

/** Ключ элемента с готовыми вопросами — чтобы список не пересоздавал его при сбросе ветки. */
private const val OPTIONS_ITEM_KEY = "clarifyOptions"

/** Блок готовых вопросов всегда первый в ленте — реплики нумеруются от следующего индекса. */
private const val OPTIONS_ITEM_INDEX = 0
