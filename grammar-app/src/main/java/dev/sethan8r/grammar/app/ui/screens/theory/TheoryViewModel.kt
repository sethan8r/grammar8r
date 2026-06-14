package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.app.domain.model.theory.TheoryListItem
import dev.sethan8r.grammar.app.domain.usecase.GetTheoryListUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** UI-state списка теории. */
data class TheoryUiState(
    val isLoading: Boolean = true,
    val items: List<TheoryListItem> = emptyList(),
)

/** Тонкий ViewModel: отдаёт готовый список из use case как [StateFlow]. Логики тут нет. */
@HiltViewModel
class TheoryViewModel @Inject constructor(
    getTheoryList: GetTheoryListUseCase,
) : ViewModel() {

    val uiState: StateFlow<TheoryUiState> = getTheoryList()
        .map { TheoryUiState(isLoading = false, items = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TheoryUiState(isLoading = true),
        )
}