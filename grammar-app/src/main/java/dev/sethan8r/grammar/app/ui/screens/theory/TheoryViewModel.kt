package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sethan8r.grammar.app.domain.model.theory.SearchGroup
import dev.sethan8r.grammar.app.domain.model.theory.TheoryListItem
import dev.sethan8r.grammar.app.domain.usecase.GetTheoryListUseCase
import dev.sethan8r.grammar.app.domain.usecase.search.SearchTheoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Что показывать вместо дерева, пока открыт поиск. */
sealed interface TheorySearchContent {
    /** Запрос ещё не введён. */
    data object Idle : TheorySearchContent

    data object NoResults : TheorySearchContent

    data class Results(val groups: List<SearchGroup>) : TheorySearchContent
}

/** UI-state списка теории. */
data class TheoryUiState(
    val isLoading: Boolean = true,
    val items: List<TheoryListItem> = emptyList(),
    val isSearchOpen: Boolean = false,
    val query: String = "",
    /** Поиск только что открыли — поле должно взять фокус и поднять клавиатуру ровно один раз. */
    val requestSearchFocus: Boolean = false,
    val searchContent: TheorySearchContent = TheorySearchContent.Idle,
)

/** Тонкий ViewModel: дерево из одного use case, выдача из другого. Логики поиска тут нет. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TheoryViewModel @Inject constructor(
    getTheoryList: GetTheoryListUseCase,
    searchTheory: SearchTheoryUseCase,
) : ViewModel() {

    private val searchInput = MutableStateFlow(SearchInput())

    val uiState: StateFlow<TheoryUiState> = combine(
        getTheoryList(),
        searchInput,
        searchInput.flatMapLatest { searchTheory(it.queryOrEmpty()) },
    ) { items, input, groups ->
        TheoryUiState(
            isLoading = false,
            items = items,
            isSearchOpen = input.isOpen,
            query = input.query,
            requestSearchFocus = input.requestFocus,
            searchContent = when {
                input.query.isBlank() -> TheorySearchContent.Idle
                groups.isEmpty() -> TheorySearchContent.NoResults
                else -> TheorySearchContent.Results(groups)
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TheoryUiState(isLoading = true),
    )

    fun onSearchOpen() = searchInput.update { it.copy(isOpen = true, requestFocus = true) }

    fun onSearchClose() = searchInput.update { SearchInput() }

    /** Крестик на непустом поле: текст стирается, но пользователь остаётся в поиске. */
    fun onQueryClear() = searchInput.update { it.copy(query = "", requestFocus = true) }

    fun onQueryChange(query: String) = searchInput.update { it.copy(query = query) }

    fun onSearchFocusConsumed() = searchInput.update { it.copy(requestFocus = false) }

    private data class SearchInput(
        val isOpen: Boolean = false,
        val query: String = "",
        val requestFocus: Boolean = false,
    ) {
        /** Закрытый поиск ничего не ищет — БД не трогается, пока пользователь не открыл строку. */
        fun queryOrEmpty(): String = if (isOpen) query else ""
    }
}
