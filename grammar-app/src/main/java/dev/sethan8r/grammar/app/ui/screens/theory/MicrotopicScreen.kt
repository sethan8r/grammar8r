package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.theory.Example
import dev.sethan8r.grammar.app.domain.model.theory.TheoryCard
import dev.sethan8r.grammar.app.ui.components.BackTopBar
import dev.sethan8r.grammar.app.ui.components.CenteredHint
import dev.sethan8r.grammar.app.ui.components.LoadingIndicator
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.components.SegmentedProgressBar
import dev.sethan8r.grammar.app.ui.components.TheoryBlocks
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
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
    viewModel: MicrotopicViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // В шапке карточки — только английское название микротемы (RU-часть двойного имени убираем).
        BackTopBar(title = uiState.title.substringBefore(" · "), onBack = onBack)

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
            )
        }
    }
}

@Composable
private fun CardPager(
    cards: List<TheoryCard>,
    completedCardIds: Set<Int>,
    onStartExercises: (cardId: Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { cards.size })
    val scope = rememberCoroutineScope()

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
            // ID текущей карточки — в маленьком закруглённом фрейме (фон как у таблиц).
            // Пройденная карточка → фрейм зелёный.
            val currentId = cards[pagerState.currentPage].id
            val idCompleted = currentId in completedCardIds
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.cornerSmall))
                    .background(if (idCompleted) CorrectGreen else CardBackground)
                    .padding(horizontal = Dimens.spaceTiny, vertical = Dimens.spaceMicro),
            ) {
                Text(
                    text = stringResource(R.string.theory_card_id, currentId),
                    color = if (idCompleted) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    // Убираем «свинцовый» отступ шрифта — фрейм по высоте облегает текст плотнее.
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                )
            }
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
            .padding(bottom = Dimens.spaceLarge),
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
        examples.forEach { example ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.cornerCard))
                    .background(CardBackground)
                    .padding(Dimens.cardPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
            ) {
                MarkdownText(text = example.en, color = TextPrimary, fontSize = 16.sp)
                Text(text = example.ru, color = TextSecondary, fontSize = 14.sp, fontStyle = FontStyle.Italic)
            }
        }
    }
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
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background),
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
