package dev.sethan8r.grammar.app.ui.screens.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.core.model.exercise.ChoiceType
import dev.sethan8r.grammar.core.model.exercise.Exercise
import dev.sethan8r.grammar.core.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.core.model.progress.CardCompletion
import dev.sethan8r.grammar.app.ui.components.CenteredHint
import dev.sethan8r.grammar.app.ui.components.LoadingIndicator
import dev.sethan8r.grammar.app.ui.components.dialog.InfoDialog
import dev.sethan8r.grammar.app.ui.components.feedback.FeedbackSnackbarHost
import dev.sethan8r.grammar.app.ui.components.feedback.rememberFeedbackSnackbarController
import dev.sethan8r.grammar.app.ui.components.progress.IdBadge
import dev.sethan8r.grammar.app.ui.components.progress.SegmentedProgressBar
import dev.sethan8r.grammar.app.ui.components.scaffold.BackTopBar
import dev.sethan8r.grammar.app.ui.components.scaffold.ExitConfirmationHandler
import dev.sethan8r.grammar.app.ui.components.scaffold.PinnedHeader
import dev.sethan8r.grammar.app.ui.components.theory.TheoryBlocks
import dev.sethan8r.grammar.app.ui.components.titleEn
import dev.sethan8r.grammar.app.ui.components.exercise.AiPlaceholderView
import dev.sethan8r.grammar.app.ui.components.exercise.CategorizationExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.MatchingExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.SingleSelectExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.SingleSelectHeader
import dev.sethan8r.grammar.app.ui.components.exercise.TableFillExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.TextInputExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.TransformationExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.TrueFalseExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.UnsupportedExerciseView
import dev.sethan8r.grammar.app.ui.components.exercise.WordArrangementExerciseView
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.bottomScrim
import dev.sethan8r.grammar.app.ui.util.pinnedHeaderTopInset

/**
 * Экран-сессия упражнений карточки (полноэкранный, без навбара). Верх: название микротемы + кнопка
 * «?» (`theorySummary`), ниже — зелёный бокс ID текущего упражнения + полоса прогресса по сегментам.
 * Тело — задание во фрейме (трясётся на ошибке). Фидбэк на неверный ответ — нижний снекбар; на
 * верный ничего не показываем. Логика — во ViewModel; экран только отображает и шлёт действия.
 */
@Composable
fun ExerciseSessionScreen(
    onFinished: (CardCompletion) -> Unit,
    onExit: () -> Unit,
    viewModel: ExerciseSessionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showExitDialog by remember { mutableStateOf(false) }
    var showSummaryDialog by remember { mutableStateOf(false) }
    val snackbar = rememberFeedbackSnackbarController()

    val wrongFirstMsg = stringResource(R.string.exercise_wrong_try_again)
    val wrongFinalMsg = stringResource(R.string.exercise_wrong_final)

    LaunchedEffect(Unit) {
        viewModel.finished.collect { onFinished(it.completion) }
    }
    LaunchedEffect(Unit) {
        // Новый «Неправильно» сразу затирает «Попробуйте ещё раз» — без очереди (см. контроллер).
        viewModel.wrongAnswer.collect { event ->
            snackbar.show(if (event.firstAttempt) wrongFirstMsg else wrongFinalMsg, Durations.wrongAnswerSnackbarMs)
        }
    }

    ExitConfirmationHandler(
        enabled = !state.isLoading,
        showDialog = showExitDialog,
        onShowDialogChange = { showExitDialog = it },
        onConfirmExit = onExit,
        title = stringResource(R.string.exercise_exit_title),
        message = stringResource(R.string.exercise_exit_message),
        confirmLabel = stringResource(R.string.exercise_exit_confirm),
        dismissLabel = stringResource(R.string.exercise_exit_cancel),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingIndicator(Modifier.fillMaxSize())
            state.total == 0 -> EmptySession(onFinish = viewModel::onNext)
            else -> SessionContent(
                state = state,
                snackbarHostState = snackbar.hostState,
                onSelectOption = viewModel::onOptionSelected,
                onTextChanged = viewModel::onTextChanged,
                onArrangementChanged = viewModel::onArrangementChanged,
                onStatementToggled = viewModel::onStatementToggled,
                onPairingChanged = viewModel::onPairingChanged,
                onCategorizationChanged = viewModel::onCategorizationChanged,
                onCheck = viewModel::onCheck,
                onNext = viewModel::onNext,
            )
        }

        PinnedHeader {
            BackTopBar(
                // Только английская часть двойного имени (как в шапке карточек микротемы).
                title = state.microtopicTitle.titleEn(),
                onBack = { showExitDialog = true },
                modifier = Modifier.height(Dimens.topBarHeight),
                actions = {
                    val helpEnabled = state.theorySummary.isNotEmpty()
                    IconButton(onClick = { showSummaryDialog = true }, enabled = helpEnabled) {
                        Icon(
                            Icons.Outlined.HelpOutline,
                            stringResource(R.string.exercise_help),
                            tint = if (helpEnabled) TextPrimary else TextSecondary,
                        )
                    }
                },
            )
            state.current?.let { exercise ->
                ProgressRow(
                    exercise = exercise,
                    answered = state.currentPassed,
                    total = state.total,
                    currentIndex = state.currentIndex,
                    onSegmentClick = if (state.cardCompleted) viewModel::onSegmentSelected else null,
                )
            }
        }
    }

    if (showSummaryDialog) {
        InfoDialog(
            title = stringResource(R.string.exercise_help),
            confirmLabel = stringResource(R.string.exercise_summary_close),
            onDismiss = { showSummaryDialog = false },
        ) {
            TheoryBlocks(blocks = state.theorySummary, containerColor = Background)
        }
    }
}

@Composable
private fun SessionContent(
    state: ExerciseSessionUiState,
    snackbarHostState: SnackbarHostState,
    onSelectOption: (Int) -> Unit,
    onTextChanged: (Int, String) -> Unit,
    onArrangementChanged: (List<String>) -> Unit,
    onStatementToggled: (Int) -> Unit,
    onPairingChanged: (List<String>) -> Unit,
    onCategorizationChanged: (Map<String, Int>) -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
) {
    val exercise = state.current ?: return
    val density = LocalDensity.current
    // Высота плавающего футера → нижний отступ скролла: последняя строка задания может уехать
    // выше кнопки; под кнопку контент заходит только при прокрутке (там его затемняет подложка).
    var footerHeight by remember { mutableStateOf(0.dp) }

    // Низ = max(клавиатура, системная полоса навигации): клавиатура скрыта → отступ держит футер над
    // навбаром; открыта → контент ужимается, кнопка «Проверить» встаёт вплотную к клавиатуре. Навбар
    // тут резервируем сами — NavHost на полноэкранных роутах его не добавляет (edge-to-edge).
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars)),
    ) {
        // Тело: задание скроллится на всю высоту — сверху уезжает под закреплённую шапку, снизу под
        // кнопку; обе затемняют проезжающий под ними контент, а не закрывают сплошной плашкой.
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.screenPadding)
                    .padding(
                        top = pinnedHeaderTopInset(extra = Dimens.progressRowHeight),
                        bottom = footerHeight,
                    ),
            ) {
                // Подпись задания — приглушённо (TextSecondary), слева под полосой, над фреймом.
                Text(
                    text = exerciseTypeLabel(exercise),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.spaceSmall),
                    color = TextSecondary,
                    fontSize = 11.sp,
                )

                // key(currentIndex): каждый шаг сессии — свежий поддерев (сброс ввода/фокуса и
                // локального состояния сборки WORD_ARRANGEMENT при переходе между упражнениями).
                key(state.currentIndex) {
                    when (exercise) {
                        is Exercise.SingleSelect -> SingleSelectExerciseView(
                            options = exercise.options,
                            selectedIndex = (state.answer as? ExerciseAnswer.SingleChoice)?.selectedIndex ?: -1,
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            explanation = exercise.explanation,
                            onSelect = onSelectOption,
                            header = { SingleSelectHeader(exercise) },
                        )
                        is Exercise.TextInput -> TextInputExerciseView(
                            exercise = exercise,
                            answer = state.answer as? ExerciseAnswer.TextAnswers,
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            onChange = onTextChanged,
                        )
                        is Exercise.TableFill -> TableFillExerciseView(
                            exercise = exercise,
                            answer = state.answer as? ExerciseAnswer.TextAnswers,
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            onChange = onTextChanged,
                        )
                        is Exercise.Transformation -> TransformationExerciseView(
                            exercise = exercise,
                            answer = state.answer as? ExerciseAnswer.TextAnswers,
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            onChange = onTextChanged,
                        )
                        is Exercise.WordArrangement -> WordArrangementExerciseView(
                            exercise = exercise,
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            onArrangementChanged = onArrangementChanged,
                        )
                        is Exercise.TrueFalse -> TrueFalseExerciseView(
                            exercise = exercise,
                            selectedIndices = (state.answer as? ExerciseAnswer.MultiChoice)?.selectedIndices ?: emptySet(),
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            onToggle = onStatementToggled,
                        )
                        is Exercise.Matching -> MatchingExerciseView(
                            exercise = exercise,
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            onOrderChanged = onPairingChanged,
                        )
                        is Exercise.Categorization -> CategorizationExerciseView(
                            exercise = exercise,
                            phase = state.phase,
                            shakeKey = state.shakeKey,
                            pulseKey = state.pulseKey,
                            onPlacementChanged = onCategorizationChanged,
                        )
                        is Exercise.Unsupported -> UnsupportedExerciseView(exercise)
                        is Exercise.AiPlaceholder -> AiPlaceholderView()
                    }
                }
            }

            // Плавающий футер: снекбар-фидбэк + кнопка на полупрозрачной подложке.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .onGloballyPositioned { footerHeight = with(density) { it.size.height.toDp() } }
                    .bottomScrim(),
            ) {
                // Уведомление — НАД кнопкой (не перекрывает «Проверить»).
                FeedbackSnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.screenPadding),
                )

                SessionFooter(
                    canProceed = state.canProceed,
                    canCheck = state.canCheck,
                    isLast = state.isLastExercise,
                    onCheck = onCheck,
                    onNext = onNext,
                )
            }
        }
    }
}

/** Подпись задания: «ТИП · краткое описание что делать» (описание по типу, из strings.xml). */
@Composable
private fun exerciseTypeLabel(exercise: Exercise): String = when (exercise) {
    is Exercise.SingleSelect -> exercise.type.name + " · " + stringResource(singleSelectDescRes(exercise))
    is Exercise.TextInput -> exercise.type.name + " · " + stringResource(R.string.exercise_desc_text_input)
    is Exercise.TableFill -> exercise.type.name + " · " + stringResource(R.string.exercise_desc_table_fill)
    is Exercise.Transformation -> exercise.type.name + " · " + stringResource(R.string.exercise_desc_transformation)
    is Exercise.WordArrangement -> exercise.type.name + " · " + stringResource(R.string.exercise_desc_word_arrangement)
    is Exercise.TrueFalse -> exercise.type.name + " · " + stringResource(R.string.exercise_desc_true_false)
    is Exercise.Matching -> exercise.type.name + " · " + stringResource(R.string.exercise_desc_matching)
    is Exercise.Categorization -> exercise.type.name + " · " + stringResource(R.string.exercise_desc_categorization)
    is Exercise.Unsupported -> exercise.type.name
    // У умного задания вместо типа — его (длинный) строковый ID; в маленький бокс он не влезает.
    is Exercise.AiPlaceholder -> exercise.exerciseId
}

private fun singleSelectDescRes(exercise: Exercise.SingleSelect): Int = when (exercise) {
    is Exercise.Choice -> when (exercise.choiceType) {
        ChoiceType.CHOICE -> R.string.exercise_desc_multiple_choice
        ChoiceType.FORWARD_CHOICE -> R.string.exercise_desc_forward_choice
        ChoiceType.REVERSE_CHOICE -> R.string.exercise_desc_reverse_choice
    }
    is Exercise.ErrorCorrection -> R.string.exercise_desc_error_correction
    is Exercise.ConstructionMeaning -> R.string.exercise_desc_construction_meaning
    is Exercise.DialogRestore -> R.string.exercise_desc_dialog_restore
    is Exercise.FindTheOdd -> R.string.exercise_desc_find_the_odd
}

/** Полоса прогресса по сегментам + зелёный бокс ID СПРАВА (как в карточках микротемы). */
@Composable
private fun ProgressRow(
    exercise: Exercise,
    answered: Boolean,
    total: Int,
    currentIndex: Int,
    onSegmentClick: ((Int) -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.progressRowHeight)
            .padding(horizontal = Dimens.screenPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
    ) {
        SegmentedProgressBar(
            total = total,
            currentIndex = currentIndex,
            modifier = Modifier.weight(1f),
            onSegmentClick = onSegmentClick,
        )
        IdBadge(
            text = when (exercise) {
                is Exercise.AiPlaceholder -> stringResource(R.string.exercise_ai_short)
                else -> stringResource(R.string.exercise_id_short, exercise.id)
            },
            highlighted = answered,
        )
    }
}

@Composable
private fun SessionFooter(
    canProceed: Boolean,
    canCheck: Boolean,
    isLast: Boolean,
    onCheck: () -> Unit,
    onNext: () -> Unit,
) {

    Column(
        modifier = Modifier.padding(
            start = Dimens.screenPadding,
            top = Dimens.sessionFooterTopGap,
            end = Dimens.screenPadding,
            bottom = Dimens.bottomBarGap12,
        ),
    ) {
        if (canProceed) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background),
            ) {
                Text(stringResource(if (isLast) R.string.exercise_finish else R.string.exercise_next))
            }
        } else {
            Button(
                onClick = onCheck,
                enabled = canCheck,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background),
            ) {
                Text(stringResource(R.string.exercise_check))
            }
        }
    }
}

@Composable
private fun EmptySession(onFinish: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenteredHint(
            stringResource(R.string.exercise_empty),
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.screenPadding),
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background),
        ) {
            Text(stringResource(R.string.exercise_finish))
        }
    }
}