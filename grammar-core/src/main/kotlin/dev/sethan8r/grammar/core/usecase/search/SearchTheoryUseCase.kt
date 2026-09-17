package dev.sethan8r.grammar.core.usecase.search

import dev.sethan8r.grammar.core.model.theory.SearchGroup
import dev.sethan8r.grammar.core.repository.TheoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Поиск по теории: снимок индекса из репозитория + ранжирование по вводимому запросу.
 *
 * Принимает поток запросов, а не строку: индекс читается из БД и готовится к поиску один раз на
 * подписку, а каждый символ лишь пересчитывает ранжирование. Подписываться только когда поиск
 * открыт — забота вызывающего (закрытый поиск БД не трогает).
 */
class SearchTheoryUseCase @Inject constructor(
    private val repository: TheoryRepository,
    private val ranker: TheorySearchRanker,
) {

    operator fun invoke(queries: Flow<String>): Flow<List<SearchGroup>> = combine(
        repository.observeSearchIndex().map(ranker::prepare),
        queries.map(String::trim).distinctUntilChanged(),
    ) { index, query ->
        if (query.isEmpty()) emptyList() else ranker.rank(index, query)
    }
}
