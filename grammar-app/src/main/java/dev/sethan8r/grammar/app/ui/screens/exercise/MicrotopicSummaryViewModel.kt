package dev.sethan8r.grammar.app.ui.screens.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.app.domain.model.progress.MicrotopicCompletionSummary
import dev.sethan8r.grammar.app.domain.repository.ProgressRepository
import dev.sethan8r.grammar.app.ui.navigation.MicrotopicSummaryRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI-state сводки: null summary = ещё грузится. */
data class MicrotopicSummaryUiState(
    val isLoading: Boolean = true,
    val summary: MicrotopicCompletionSummary? = null,
)

@HiltViewModel
class MicrotopicSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    progressRepository: ProgressRepository,
) : ViewModel() {

    private val microtopicId: Int = savedStateHandle.toRoute<MicrotopicSummaryRoute>().microtopicId

    private val _uiState = MutableStateFlow(MicrotopicSummaryUiState())
    val uiState: StateFlow<MicrotopicSummaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val summary = progressRepository.getMicrotopicSummary(microtopicId)
            _uiState.value = MicrotopicSummaryUiState(isLoading = false, summary = summary)
        }
    }
}