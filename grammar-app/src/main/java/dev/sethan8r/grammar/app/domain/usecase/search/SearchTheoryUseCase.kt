package dev.sethan8r.grammar.app.domain.usecase.search

import dev.sethan8r.grammar.app.domain.model.theory.SearchGroup
import dev.sethan8r.grammar.app.domain.repository.TheoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Поиск по теории: индекс из репозитория + ранжирование. Пустой запрос не трогает БД —
 * индекс подтягивается только когда пользователь начал печатать.
 */
class SearchTheoryUseCase @Inject constructor(
    private val repository: TheoryRepository,
    private val ranker: TheorySearchRanker,
) {

    operator fun invoke(query: String): Flow<List<SearchGroup>> =
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.observeSearchIndex().map { index -> ranker.rank(index, query) }
        }
}
