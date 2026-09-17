package dev.sethan8r.grammar.app.ui.screens.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.core.model.exercise.Exercise
import dev.sethan8r.grammar.core.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.core.model.exercise.ExerciseRef
import dev.sethan8r.grammar.core.model.progress.CardCompletion
import dev.sethan8r.grammar.core.model.theory.TheoryBlock
import dev.sethan8r.grammar.core.repository.ExerciseRepository
import dev.sethan8r.grammar.core.repository.ProgressRepository
import dev.sethan8r.grammar.core.usecase.ExerciseEvaluator
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
    val theorySummary: List<TheoryBlock> = emptyList(),
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
        val theorySummary: List<TheoryBlock> = emptyList(),
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

    /**
     * Упражнения, чью строку результата завёл ИМЕННО этот заход, — только их верный второй ответ
     * имеет право поднять до верного. Живёт в памяти сессии: ушёл с экрана — право потеряно, поэтому
     * перезаход не даёт переиграть результат.
     */
    private val recordedHere = mutableSetOf<ExerciseRef>()

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

    /** Изменение текста в ячейке/пункте (TextInput, TableFill, Transformation — все на [ExerciseAnswer.TextAnswers]). */
    fun onTextChanged(itemIndex: Int, value: String) {
        if (!answerDelegate.state.value.isEditable) return
        val current = (answer.value as? ExerciseAnswer.TextAnswers)?.inputs ?: return
        answer.value = ExerciseAnswer.TextAnswers(
            current.toMutableList().also { it[itemIndex] = value },
        )
    }

    /** Изменение собранного предложения (WORD_ARRANGEMENT): рендерер шлёт текущий порядок текстов чипов. */
    fun onArrangementChanged(tokens: List<String>) {
        if (!answerDelegate.state.value.isEditable) return
        answer.value = ExerciseAnswer.WordOrder(tokens)
    }

    /** Тоггл утверждения (TRUE_FALSE multi-select): добавить/убрать индекс из набора выбранных. */
    fun onStatementToggled(index: Int) {
        if (!answerDelegate.state.value.isEditable) return
        val current = (answer.value as? ExerciseAnswer.MultiChoice)?.selectedIndices ?: emptySet()
        answer.value = ExerciseAnswer.MultiChoice(
            if (index in current) current - index else current + index,
        )
    }

    /** Изменение порядка правой колонки (MATCHING): рендерер шлёт тексты правых элементов сверху вниз. */
    fun onPairingChanged(rightOrder: List<String>) {
        if (!answerDelegate.state.value.isEditable) return
        answer.value = ExerciseAnswer.Pairing(rightOrder)
    }

    /** Изменение раскладки по колонкам (CATEGORIZATION): рендерер шлёт карту «элемент → индекс колонки». */
    fun onCategorizationChanged(placement: Map<String, Int>) {
        if (!answerDelegate.state.value.isEditable) return
        answer.value = ExerciseAnswer.Buckets(placement)
    }

    /**
     * Проверка ответа: верный — молча; неверный — тряска + событие уведомления.
     *
     * Запись результата принадлежит первому заходу на упражнение: первый ответ создаёт строку (в т.ч.
     * неверную — чтобы выход из карточки её не отменил), верный второй ответ поднимает её до верной.
     * Упражнение, пройденное в прошлый заход, статистику уже не меняет (анти-чит) — обе попытки
     * работают как тренировка.
     */
    fun onCheck() {
        val current = uiState.value.current ?: return
        val firstAttempt = answerDelegate.state.value.attemptsUsed == 0
        val correct = ExerciseEvaluator.isCorrect(current, answer.value)
        answerDelegate.submit(correct)
        refOf(current)?.let { ref ->
            when {
                ref in content.value.passed -> Unit // результат уже принадлежит прошлому заходу
                firstAttempt -> {
                    recordedHere += ref
                    viewModelScope.launch {
                        progressRepository.recordExerciseResult(cardId, ref.type, ref.id, correctFirstTry = correct)
                    }
                }
                correct && ref in recordedHere -> viewModelScope.launch {
                    progressRepository.markExerciseCorrect(cardId, ref.type, ref.id)
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
        is Exercise.SingleSelect -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.TextInput -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.TableFill -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.Transformation -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.WordArrangement -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.TrueFalse -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.Matching -> ExerciseRef(exercise.type, exercise.id)
        is Exercise.Categorization -> ExerciseRef(exercise.type, exercise.id)
        else -> null
    }

    /** Сбрасывает ввод, фазу и тряску под упражнение [index]; плашки-сегменты сразу проходимы. */
    private fun prepareForIndex(index: Int) {
        // Сброс триггеров тряски/пульса на новом упражнении: иначе свежесмонтированный фрейм другого
        // типа увидел бы ключ>0 и проиграл бы анимацию на входе (баг при переходе после ответа).
        feedback.value = FeedbackTriggers()
        val exercise = content.value.exercises.getOrNull(index)
        answer.value = when (exercise) {
            is Exercise.SingleSelect -> ExerciseAnswer.SingleChoice()
            is Exercise.TextInput -> ExerciseAnswer.TextAnswers(List(exercise.items.size) { "" })
            is Exercise.TableFill -> ExerciseAnswer.TextAnswers(List(exercise.rows.size) { "" })
            is Exercise.Transformation -> ExerciseAnswer.TextAnswers(List(exercise.items.size) { "" })
            is Exercise.WordArrangement -> ExerciseAnswer.WordOrder(emptyList())
            is Exercise.TrueFalse -> ExerciseAnswer.MultiChoice()
            // Matching/Categorization стартуют пустыми — стартовую раскладку (шафл) пришлёт рендерер.
            is Exercise.Matching -> ExerciseAnswer.Pairing(emptyList())
            is Exercise.Categorization -> ExerciseAnswer.Buckets(emptyMap())
            else -> null
        }
        answerDelegate.start(skipAnswering = exercise is Exercise.Placeholder)
    }

    private fun canCheck(exercise: Exercise?, answer: ExerciseAnswer?): Boolean = when (exercise) {
        // Нужен выбранный вариант.
        is Exercise.SingleSelect -> (answer as? ExerciseAnswer.SingleChoice)?.selectedIndex?.let { it >= 0 } == true
        // Нельзя проверять, пока не заполнены все пункты (исключение — пункт с пустым ответом «—»).
        is Exercise.TextInput -> {
            val inputs = (answer as? ExerciseAnswer.TextAnswers)?.inputs
            inputs != null && inputs.size == exercise.items.size &&
                exercise.items.indices.all { i -> inputs[i].isNotBlank() || exercise.items[i].answer.isBlank() }
        }
        // All-or-nothing: проверка доступна, когда заполнены все ячейки/примеры.
        is Exercise.TableFill -> (answer as? ExerciseAnswer.TextAnswers)?.inputs?.all { it.isNotBlank() } == true
        is Exercise.Transformation -> (answer as? ExerciseAnswer.TextAnswers)?.inputs?.all { it.isNotBlank() } == true
        // Хотя бы одно слово собрано.
        is Exercise.WordArrangement -> (answer as? ExerciseAnswer.WordOrder)?.tokens?.isNotEmpty() == true
        // Хотя бы одно утверждение отмечено.
        is Exercise.TrueFalse -> (answer as? ExerciseAnswer.MultiChoice)?.selectedIndices?.isNotEmpty() == true
        // Стартовая раскладка правой колонки получена (переставлять можно всегда).
        is Exercise.Matching -> (answer as? ExerciseAnswer.Pairing)?.rightOrder?.size == exercise.pairs.size
        // Все элементы разложены по колонкам (пул пуст).
        is Exercise.Categorization -> {
            val placement = (answer as? ExerciseAnswer.Buckets)?.placement
            placement != null && placement.size == exercise.categories.sumOf { it.items.size }
        }
        else -> false
    }
}