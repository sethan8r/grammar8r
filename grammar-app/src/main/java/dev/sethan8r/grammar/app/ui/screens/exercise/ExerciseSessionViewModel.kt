package dev.sethan8r.grammar.app.ui.screens.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseRef
import dev.sethan8r.grammar.app.domain.model.progress.CardCompletion
import dev.sethan8r.grammar.app.domain.repository.ExerciseRepository
import dev.sethan8r.grammar.app.domain.repository.ProgressRepository
import dev.sethan8r.grammar.app.domain.usecase.ExerciseEvaluator
import dev.sethan8r.grammar.app.ui.navigation.ExerciseSessionRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI-state одного шага сессии: текущее упражнение + фаза ответа + прогресс. */
data class ExerciseSessionUiState(
    val isLoading: Boolean = true,
    val microtopicTitle: String = "",
    val theorySummary: String = "",
    val total: Int = 0,
    /** Индекс текущего упражнения (0-based) — для полосы прогресса. */
    val currentIndex: Int = 0,
    val current: Exercise? = null,
    val answer: ExerciseAnswer? = null,
    val phase: AnswerPhase = AnswerPhase.ANSWERING,
    val isEditable: Boolean = true,
    val canCheck: Boolean = false,
    val canProceed: Boolean = false,
    val isLastExercise: Boolean = false,
    /** Текущее упражнение пройдено (ответ дан сейчас, либо есть результат в БД, либо карточка пройдена) → зелёный ID. */
    val currentPassed: Boolean = false,
    /** Карточка пройдена ранее → полоса прогресса кликабельна (прыжки по упражнениям). */
    val cardCompleted: Boolean = false,
    /** Счётчик тряски фрейма: растёт на каждый неверный ответ (фрейм реагирует на изменение). */
    val shakeKey: Int = 0,
    /** Счётчик пульса фрейма: растёт на каждый верный ответ (фрейм увеличивается и возвращается). */
    val pulseKey: Int = 0,
)

/** Одноразовое событие завершения сессии (карточка пройдена) — навигацию делает экран. */
data class ExerciseSessionFinished(val completion: CardCompletion)

/** Одноразовое событие неверного ответа → экран показывает уведомление (на верный — ничего). */
data class WrongAnswerEvent(val firstAttempt: Boolean)

@HiltViewModel
class ExerciseSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val exerciseRepository: ExerciseRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val cardId: Int = savedStateHandle.toRoute<ExerciseSessionRoute>().cardId

    private data class Content(
        val isLoading: Boolean = true,
        val microtopicTitle: String = "",
        val theorySummary: String = "",
        val exercises: List<Exercise> = emptyList(),
        val cardCompleted: Boolean = false,
        /** Упражнения карточки с уже записанным результатом (для зелёного ID при заходе). */
        val passed: Set<ExerciseRef> = emptySet(),
    )

    /** Триггеры анимации-фидбэка фрейма (растут на ответ): тряска — на неверный, пульс — на верный. */
    private data class FeedbackTriggers(val shakeKey: Int = 0, val pulseKey: Int = 0)

    private val content = MutableStateFlow(Content())
    private val currentIndex = MutableStateFlow(0)
    private val answer = MutableStateFlow<ExerciseAnswer?>(null)
    private val feedback = MutableStateFlow(FeedbackTriggers())
    private val answerDelegate = AnswerDelegate()

    private val _finished = Channel<ExerciseSessionFinished>(Channel.BUFFERED)
    val finished: Flow<ExerciseSessionFinished> = _finished.receiveAsFlow()

    private val _wrongAnswer = Channel<WrongAnswerEvent>(Channel.BUFFERED)
    val wrongAnswer: Flow<WrongAnswerEvent> = _wrongAnswer.receiveAsFlow()

    val uiState: StateFlow<ExerciseSessionUiState> =
        combine(content, currentIndex, answer, answerDelegate.state, feedback) { content, index, answer, delegate, feedback ->
            val current = content.exercises.getOrNull(index)
            val ref = refOf(current)
            ExerciseSessionUiState(
                isLoading = content.isLoading,
                microtopicTitle = content.microtopicTitle,
                theorySummary = content.theorySummary,
                total = content.exercises.size,
                currentIndex = index,
                current = current,
                answer = answer,
                phase = delegate.phase,
                isEditable = delegate.isEditable,
                canCheck = delegate.isEditable && canCheck(current, answer),
                canProceed = delegate.canProceed,
                isLastExercise = index >= content.exercises.lastIndex,
                currentPassed = delegate.canProceed || content.cardCompleted || (ref != null && ref in content.passed),
                cardCompleted = content.cardCompleted,
                shakeKey = feedback.shakeKey,
                pulseKey = feedback.pulseKey,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExerciseSessionUiState(isLoading = true),
        )

    init {
        viewModelScope.launch {
            val session = exerciseRepository.getSession(cardId)
            content.value = Content(
                isLoading = false,
                microtopicTitle = session.microtopicTitle,
                theorySummary = session.theorySummary,
                exercises = session.exercises,
                cardCompleted = progressRepository.isCardCompleted(cardId),
                passed = progressRepository.getPassedExercises(cardId),
            )
            prepareForIndex(0)
        }
    }

    /** Выбор варианта (Choice) — только пока ответ редактируем. */
    fun onOptionSelected(optionIndex: Int) {
        if (answerDelegate.state.value.isEditable) {
            answer.value = ExerciseAnswer.SingleChoice(optionIndex)
        }
    }

    /** Изменение текста в пункте TextInput. */
    fun onTextChanged(itemIndex: Int, value: String) {
        if (!answerDelegate.state.value.isEditable) return
        val current = (answer.value as? ExerciseAnswer.TextAnswers)?.inputs ?: return
        answer.value = ExerciseAnswer.TextAnswers(
            current.toMutableList().also { it[itemIndex] = value },
        )
    }

    /**
     * Проверка ответа: верный — молча; неверный — тряска + событие уведомления. Результат упражнения
     * фиксируется в БД в момент ПЕРВОГО ответа (write-once, анти-чит) — независимо от верности.
     */
    fun onCheck() {
        val current = uiState.value.current ?: return
        val firstAttempt = answerDelegate.state.value.attemptsUsed == 0
        val correct = ExerciseEvaluator.isCorrect(current, answer.value)
        answerDelegate.submit(correct)
        if (firstAttempt) {
            refOf(current)?.let { ref ->
                viewModelScope.launch {
                    progressRepository.recordExerciseResult(cardId, ref.type, ref.id, correctFirstTry = correct)
                }
            }
        }
        if (correct) {
            feedback.update { it.copy(pulseKey = it.pulseKey + 1) }
        } else {
            feedback.update { it.copy(shakeKey = it.shakeKey + 1) }
            viewModelScope.launch { _wrongAnswer.send(WrongAnswerEvent(firstAttempt = firstAttempt)) }
        }
    }

    /** «Далее»: следующее упражнение либо завершение карточки. */
    fun onNext() {
        val exercises = content.value.exercises
        val index = currentIndex.value
        if (index < exercises.lastIndex) {
            currentIndex.value = index + 1
            prepareForIndex(index + 1)
        } else {
            finishCard()
        }
    }

    /** Прыжок по полосе прогресса — только когда карточка уже пройдена (повторное прохождение). */
    fun onSegmentSelected(index: Int) {
        if (!content.value.cardCompleted) return
        if (index !in content.value.exercises.indices) return
        currentIndex.value = index
        prepareForIndex(index)
    }

    private fun finishCard() {
        viewModelScope.launch {
            val completion = progressRepository.completeCard(cardId)
            _finished.send(ExerciseSessionFinished(completion))
        }
    }

    /** Ссылка на хардкод-упражнение (для записи результата / зелёного ID); у плашек её нет. */
    private fun refOf(exercise: Exercise?): ExerciseRef? = when (exercise) {
        is Exercise.Choice -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.TextInput -> ExerciseRef(exercise.type, exercise.id)
        else -> null
    }

    /** Сбрасывает ввод, фазу и тряску под упражнение [index]; плашки-сегменты сразу проходимы. */
    private fun prepareForIndex(index: Int) {
        // Сброс триггеров тряски/пульса на новом упражнении: иначе свежесмонтированный фрейм другого
        // типа увидел бы ключ>0 и проиграл бы анимацию на входе (баг при переходе после ответа).
        feedback.value = FeedbackTriggers()
        val exercise = content.value.exercises.getOrNull(index)
        answer.value = when (exercise) {
            is Exercise.Choice -> ExerciseAnswer.SingleChoice()
            is Exercise.TextInput -> ExerciseAnswer.TextAnswers(List(exercise.items.size) { "" })
            else -> null
        }
        answerDelegate.start(skipAnswering = exercise is Exercise.Placeholder)
    }

    private fun canCheck(exercise: Exercise?, answer: ExerciseAnswer?): Boolean = when (exercise) {
        // Нужен выбранный вариант.
        is Exercise.Choice -> (answer as? ExerciseAnswer.SingleChoice)?.selectedIndex?.let { it >= 0 } == true
        // Пустой ответ бывает валиден (Ответ: «—»), поэтому проверку разрешаем всегда.
        is Exercise.TextInput -> true
        else -> false
    }
}