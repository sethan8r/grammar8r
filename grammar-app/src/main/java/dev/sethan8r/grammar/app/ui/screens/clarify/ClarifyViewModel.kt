package dev.sethan8r.grammar.app.ui.screens.clarify

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.core.model.common.ApiResult
import dev.sethan8r.grammar.core.model.exercise.ClarificationTurn
import dev.sethan8r.grammar.core.model.theory.ClarificationContext
import dev.sethan8r.grammar.core.repository.AiExerciseRepository
import dev.sethan8r.grammar.core.repository.EntitlementsProvider
import dev.sethan8r.grammar.core.repository.TheoryRepository
import dev.sethan8r.grammar.app.ui.navigation.ClarifyRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Один обмен в треде: вопрос пользователя и ответ на него. [answer] `null` — ответ ещё в пути;
 * [failed] — запрос не удался (ответа не будет, вопрос лимит не потратил).
 */
data class ClarifyExchange(
    val id: Long,
    val question: String,
    val answer: String? = null,
    val failed: Boolean = false,
)

/** UI-state экрана уточнения: шапка, тред, готовые вопросы, поле ввода и остаток лимита. */
data class ClarifyUiState(
    val isLoading: Boolean = true,
    val microtopicTitle: String = "",
    val cardTitle: String = "",
    /** Готовые вопросы карточки — показываются, пока тред пуст. */
    val options: List<String> = emptyList(),
    val exchanges: List<ClarifyExchange> = emptyList(),
    val input: String = "",
    val aiRequestsLeft: Int = 0,
    val isAiUnlimited: Boolean = false,
) {
    /** Ждём ответ на последний вопрос — новый задать нельзя. */
    val isAwaitingAnswer: Boolean get() = exchanges.lastOrNull()?.let { it.answer == null && !it.failed } == true

    /** Ветка упёрлась в потолок обменов — дальше только новый вопрос с чистого листа. */
    val isBranchExhausted: Boolean get() = exchanges.count { it.answer != null } >= MAX_BRANCH_DEPTH

    val isLimitReached: Boolean get() = !isAiUnlimited && aiRequestsLeft <= 0

    /** Можно ли отправить новый вопрос. */
    val canAsk: Boolean get() = !isLoading && !isAwaitingAnswer && !isBranchExhausted && !isLimitReached
}

/**
 * Тред уточнения по карточке теории. Держит ветку вопросов, остаток дневного лимита и потолок
 * глубины; текст ответа берёт у [AiExerciseRepository] (в Фазе 1 — заглушка с задержкой).
 *
 * Каждый заданный вопрос списывает AI-запрос, поэтому глубина ветки ограничена
 * [MAX_BRANCH_DEPTH] обменами: дальше пользователю предлагается начать новый вопрос или уйти
 * к упражнениям.
 */
@HiltViewModel
class ClarifyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val theoryRepository: TheoryRepository,
    private val aiRepository: AiExerciseRepository,
    private val entitlementsProvider: EntitlementsProvider,
) : ViewModel() {

    private val cardId: Int = savedStateHandle.toRoute<ClarifyRoute>().cardId

    /** Состояние самого треда (контекст карточки грузится один раз при входе). */
    private data class ThreadState(
        val context: ClarificationContext? = null,
        val exchanges: List<ClarifyExchange> = emptyList(),
        val input: String = "",
    )

    private val thread = MutableStateFlow(ThreadState())
    private var nextExchangeId = 0L

    val uiState: StateFlow<ClarifyUiState> = combine(
        thread,
        entitlementsProvider.entitlements,
    ) { threadState, entitlements ->
        ClarifyUiState(
            isLoading = threadState.context == null,
            microtopicTitle = threadState.context?.microtopicTitle.orEmpty(),
            cardTitle = threadState.context?.cardTitle.orEmpty(),
            options = threadState.context?.options.orEmpty(),
            exchanges = threadState.exchanges,
            input = threadState.input,
            aiRequestsLeft = entitlements.aiRequestsLeft,
            isAiUnlimited = entitlements.isAiUnlimited,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ClarifyUiState(),
    )

    init {
        viewModelScope.launch {
            val context = theoryRepository.getClarificationContext(cardId)
            thread.update { it.copy(context = context) }
        }
    }

    fun onInputChange(value: String) {
        thread.update { it.copy(input = value.take(MAX_QUESTION_LENGTH)) }
    }

    /** Отправить вопрос из поля ввода. */
    fun askTyped() {
        val question = thread.value.input.trim()
        if (question.isEmpty()) return
        thread.update { it.copy(input = "") }
        ask(question)
    }

    /** Отправить один из готовых вопросов карточки. */
    fun ask(question: String) {
        val context = thread.value.context ?: return
        if (!uiState.value.canAsk) return

        val id = nextExchangeId++
        val history = thread.value.exchanges.mapNotNull { exchange ->
            exchange.answer?.let { ClarificationTurn(exchange.question, it) }
        }
        thread.update { it.copy(exchanges = it.exchanges + ClarifyExchange(id, question)) }

        viewModelScope.launch {
            val result = aiRepository.clarify(
                cardId = context.cardId,
                cardTheory = context.theoryText,
                userQuestion = question,
                history = history,
            )
            when (result) {
                is ApiResult.Success -> {
                    entitlementsProvider.consumeAiRequest()
                    updateExchange(id) { it.copy(answer = result.data.answer) }
                }
                // Ответа нет — запрос не состоялся, лимит не трогаем.
                is ApiResult.Failure -> updateExchange(id) { it.copy(failed = true) }
            }
        }
    }

    /** Стереть тред: снова показываются готовые вопросы карточки, контекст прошлой ветки не тянем. */
    fun startNewBranch() {
        thread.update { it.copy(exchanges = emptyList(), input = "") }
    }

    private fun updateExchange(id: Long, transform: (ClarifyExchange) -> ClarifyExchange) {
        thread.update { state ->
            state.copy(exchanges = state.exchanges.map { if (it.id == id) transform(it) else it })
        }
    }
}

/** Сколько обменов «вопрос → ответ» допускается в одной ветке, прежде чем предложить начать заново. */
private const val MAX_BRANCH_DEPTH = 3

/** Потолок длины вопроса — столько же принимает сервер (лишнее он обрезает). */
private const val MAX_QUESTION_LENGTH = 300
