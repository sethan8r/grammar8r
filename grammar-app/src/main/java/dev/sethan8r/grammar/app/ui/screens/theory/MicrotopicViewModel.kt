package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.app.domain.model.progress.CardCompletion
import dev.sethan8r.grammar.app.domain.model.theory.TheoryCard
import dev.sethan8r.grammar.app.domain.repository.ProgressRepository
import dev.sethan8r.grammar.app.domain.repository.TheoryRepository
import dev.sethan8r.grammar.app.ui.navigation.MicrotopicRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI-state экрана микротемы: заголовок + карточки + id пройденных карточек. */
data class MicrotopicUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val cards: List<TheoryCard> = emptyList(),
    val completedCardIds: Set<Int> = emptySet(),
)

@HiltViewModel
class MicrotopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: TheoryRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val args: MicrotopicRoute = savedStateHandle.toRoute()

    val uiState: StateFlow<MicrotopicUiState> = repository.observeMicrotopicCards(args.microtopicId)
        .map {
            MicrotopicUiState(
                isLoading = false,
                title = it.microtopicTitle,
                cards = it.cards,
                completedCardIds = it.completedCardIds,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MicrotopicUiState(isLoading = true),
        )

    /** Одноразовое событие отметки карточки пройденной (кнопка «Завершить карточку» у карточек без заданий). */
    private val _cardCompleted = Channel<CardCompletion>(Channel.BUFFERED)
    val cardCompleted: Flow<CardCompletion> = _cardCompleted.receiveAsFlow()

    /**
     * Отмечает карточку без заданий пройденной (write-once, та же точка записи, что и сессия упражнений)
     * и шлёт результат экрану: тот листает на следующую карточку либо выходит назад, если она последняя.
     */
    fun completeCard(cardId: Int) {
        viewModelScope.launch {
            _cardCompleted.send(progressRepository.completeCard(cardId))
        }
    }
}