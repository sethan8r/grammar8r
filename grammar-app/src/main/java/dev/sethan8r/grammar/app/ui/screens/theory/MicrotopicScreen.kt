package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.theory.Example
import dev.sethan8r.grammar.app.domain.model.theory.TheoryCard
import dev.sethan8r.grammar.app.ui.components.CenteredHint
import dev.sethan8r.grammar.app.ui.components.LoadingIndicator
import dev.sethan8r.grammar.app.ui.components.progress.IdBadge
import dev.sethan8r.grammar.app.ui.components.progress.SegmentedProgressBar
import dev.sethan8r.grammar.app.ui.components.scaffold.BackTopBar
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.components.theory.TheoryBlocks
import dev.sethan8r.grammar.app.ui.components.titleEn
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.scrollBottomInset
import kotlinx.coroutines.launch

/**
 * Экран микротемы — карточки теории. Свайпы отключены: листание — кнопкой «Перейти к заданиям»
 * (на Шаге E она просто двигает к следующей карточке; в Фазе F поведёт в упражнения). Прогресс
 * сверху — сегментная полоса по числу карточек. Шаг E — только чтение, прогресс не пишется.
 */
@Composable
fun MicrotopicScreen(
    onBack: () -> Unit,
    onStartExercises: (cardId: Int) -> Unit,
    advanceAfterCardId: Int? = null,
    onAdvanceConsumed: () -> Unit = {},
    viewModel: MicrotopicViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // В шапке карточки — только английское название микротемы (RU-часть двойного имени убираем).
        BackTopBar(title = uiState.title.titleEn(), onBack = onBack)

        when {
            uiState.isLoading -> LoadingIndicator(Modifier.fillMaxSize())
            uiState.cards.isEmpty() -> CenteredHint(
                stringResource(R.string.theory_empty),
                Modifier.fillMaxSize(),
            )

            else -> CardPager(
                cards = uiState.cards,
                completedCardIds = uiState.completedCardIds,
                onStartExercises = onStartExercises,
                advanceAfterCardId = advanceAfterCardId,
                onAdvanceConsumed = onAdvanceConsumed,
            )
        }
    }
}

@Composable
private fun CardPager(
    cards: List<TheoryCard>,
    completedCardIds: Set<Int>,
    onStartExercises: (cardId: Int) -> Unit,
    advanceAfterCardId: Int?,
    onAdvanceConsumed: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { cards.size })
    val scope = rememberCoroutineScope()

    // Вернулись из сессии с прохождением карточки (микротема не закончена) → листаем на следующую.
    LaunchedEffect(advanceAfterCardId, cards) {
        val cardId = advanceAfterCardId ?: return@LaunchedEffect
        val index = cards.indexOfFirst { it.id == cardId }
        if (index in 0 until cards.lastIndex) {
            pagerState.animateScrollToPage(index + 1)
        }
        onAdvanceConsumed()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
        ) {
            SegmentedProgressBar(
                total = cards.size,
                currentIndex = pagerState.currentPage,
                modifier = Modifier.weight(1f),
                // Тап — мгновенный переход (без анимации). Запрет перепрыгивания (completion-based):
                // доступна любая ПРОЙДЕННАЯ карточка (назад) + первая непройденная («следующая на
                // очереди»). Дальше неё — нельзя, пока карточка не засчитана в БД (Шаг F).
                onSegmentClick = { index ->
                    val frontier = cards.indexOfFirst { it.id !in completedCardIds }
                    val reachable = cards[index].id in completedCardIds || index == frontier
                    if (reachable) {
                        scope.launch { pagerState.scrollToPage(index) }
                    }
                },
            )
            // ID текущей карточки — общий бокс; пройденная карточка → зелёный.
            val currentId = cards[pagerState.currentPage].id
            IdBadge(
                text = stringResource(R.string.theory_card_id, currentId),
                highlighted = currentId in completedCardIds,
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false,
        ) { page ->
            CardPage(
                card = cards[page],
                isCompleted = cards[page].id in completedCardIds,
                // «Перейти к заданиям» открывает сессию упражнений ЭТОЙ карточки. Перелистнуть к
                // следующей карточке можно полосой прогресса (после прохождения текущей — frontier
                // сдвигается). Свайпов нет (CLAUDE → «Поиск и повторное прохождение»).
                onPrimary = { onStartExercises(cards[page].id) },
            )
        }
    }
}

@Composable
private fun CardPage(card: TheoryCard, isCompleted: Boolean, onPrimary: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.screenPadding)
            .padding(bottom = scrollBottomInset()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge),
    ) {
        MarkdownText(
            text = card.title,
            modifier = Modifier.fillMaxWidth(),
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        TheoryBlocks(card.blocks)

        if (card.examples.isNotEmpty()) {
            ExamplesSection(card.examples)
        }

        CardActions(isCompleted = isCompleted, onPrimary = onPrimary)
    }
}

@Composable
private fun ExamplesSection(examples: List<Example>) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        Text(
            text = stringResource(R.string.theory_examples_title),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny)) {
            examples.forEachIndexed { index, example ->
                val shape = exampleFrameShape(index, examples.size)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background, shape)
                        .border(Dimens.exampleBorderWidth, Elevated, shape)
                        .padding(Dimens.spaceLarge),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
                ) {
                    MarkdownText(text = example.en, color = TextPrimary, fontSize = 16.sp)
                    Text(
                        text = example.ru,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

/**
 * Форма фрейма примера по позиции в списке: внешний силуэт всего списка скруглён, внутренние
 * стыки — прямые. Первый — только верхние углы, последний — только нижние, средние — без
 * скругления, единственный (он же первый и последний) — все четыре.
 */
private fun exampleFrameShape(index: Int, count: Int): RoundedCornerShape {
    val r = Dimens.cornerCard
    val isFirst = index == 0
    val isLast = index == count - 1
    return RoundedCornerShape(
        topStart = if (isFirst) r else 0.dp,
        topEnd = if (isFirst) r else 0.dp,
        bottomStart = if (isLast) r else 0.dp,
        bottomEnd = if (isLast) r else 0.dp,
    )
}

/**
 * Кнопки внизу карточки (закладка system пользователя): «Не совсем понял» (всегда сверху, Фаза 3 —
 * задизейблено), «Перейти к заданиям» (основная), и «Перейти к умному заданию» — только если
 * карточка уже пройдена (Фаза 3 — задизейблено). На Шаге E пройденных карточек нет → третья кнопка
 * не показывается; основная просто листает дальше.
 */
@Composable
private fun CardActions(isCompleted: Boolean, onPrimary: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        OutlinedButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.theory_clarify_button))
        }
        Button(
            onClick = onPrimary,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Background
            ),
        ) {
            Text(text = stringResource(R.string.theory_go_to_exercises))
        }
        if (isCompleted) {
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.theory_go_to_ai_exercise))
            }
        }
    }
}
