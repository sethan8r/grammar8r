package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.app.domain.model.TheoryCard
import dev.sethan8r.grammar.app.domain.repository.TheoryRepository
import dev.sethan8r.grammar.app.ui.navigation.MicrotopicRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
}