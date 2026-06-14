package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicSummary
import dev.sethan8r.grammar.app.domain.repository.TheoryRepository
import dev.sethan8r.grammar.app.ui.navigation.TopicRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** UI-state экрана темы: заголовок темы + микротемы с прогрессом. */
data class TopicUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val microtopics: List<MicrotopicSummary> = emptyList(),
)

@HiltViewModel
class TopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: TheoryRepository,
) : ViewModel() {

    private val args: TopicRoute = savedStateHandle.toRoute()

    val uiState: StateFlow<TopicUiState> = repository.observeTopicMicrotopics(args.topicId)
        .map { TopicUiState(isLoading = false, title = it.topicTitle, microtopics = it.microtopics) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TopicUiState(isLoading = true),
        )
}