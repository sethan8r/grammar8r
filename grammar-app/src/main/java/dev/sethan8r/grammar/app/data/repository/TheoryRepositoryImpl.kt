package dev.sethan8r.grammar.app.data.repository

import dev.sethan8r.grammar.app.data.local.content.dao.TheoryDao
import dev.sethan8r.grammar.app.data.local.user.dao.ProgressDao
import dev.sethan8r.grammar.app.data.mapper.TheoryContentMapper
import dev.sethan8r.grammar.app.domain.model.theory.ClarificationContext
import dev.sethan8r.grammar.app.domain.model.theory.IndexedMicrotopic
import dev.sethan8r.grammar.app.domain.model.theory.IndexedTopic
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicCards
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicState
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicSummary
import dev.sethan8r.grammar.app.domain.model.theory.SearchIndex
import dev.sethan8r.grammar.app.domain.model.theory.TheoryCategory
import dev.sethan8r.grammar.app.domain.model.theory.TheoryData
import dev.sethan8r.grammar.app.domain.model.theory.TheoryMicrotopicRef
import dev.sethan8r.grammar.app.domain.model.theory.TheoryTopic
import dev.sethan8r.grammar.app.domain.model.theory.TopicMicrotopics
import dev.sethan8r.grammar.app.domain.repository.TheoryRepository
import dev.sethan8r.grammar.app.domain.usecase.search.SearchKeywords
import dev.sethan8r.grammar.app.domain.usecase.theory.TheoryPlainText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Объединяет дерево теории (content.db) с прогрессом (user.db) и маппит сущности в домен.
 * Логика репозитория = combine источников + маппинг (включая разбор JSON через [mapper]).
 */
class TheoryRepositoryImpl @Inject constructor(
    private val theoryDao: TheoryDao,
    private val progressDao: ProgressDao,
    private val mapper: TheoryContentMapper,
) : TheoryRepository {

    override fun observeTheoryData(): Flow<TheoryData> = combine(
        theoryDao.getCategories(),
        theoryDao.getTopics(),
        theoryDao.getAllMicrotopics(),
        progressDao.getAllMicrotopicProgress(),
    ) { categories, topics, microtopics, progress ->
        TheoryData(
            categories = categories.map { TheoryCategory(it.id, it.title, it.description, it.order) },
            topics = topics.map {
                TheoryTopic(it.id, it.title, it.description, it.isPretopic, it.categoryId, it.order)
            },
            microtopicsByTopic = microtopics
                .groupBy { it.topicId }
                .mapValues { (_, list) ->
                    list.sortedBy { it.order }.map { TheoryMicrotopicRef(it.id, it.title, it.order) }
                },
            completedMicrotopicIds = progress.filter { it.isCompleted }.map { it.microtopicId }.toSet(),
        )
    }

    override fun observeTopicMicrotopics(topicId: Int): Flow<TopicMicrotopics> = combine(
        theoryDao.getTopics(),
        theoryDao.getMicrotopics(topicId),
        progressDao.getAllMicrotopicProgress(),
    ) { topics, microtopics, progress ->
        val completed = progress.filter { it.isCompleted }.map { it.microtopicId }.toSet()
        TopicMicrotopics(
            topicTitle = topics.firstOrNull { it.id == topicId }?.title.orEmpty(),
            microtopics = microtopics.map {
                MicrotopicSummary(
                    id = it.id,
                    title = it.title,
                    state = if (it.id in completed) MicrotopicState.COMPLETED else MicrotopicState.AVAILABLE,
                )
            },
        )
    }

    override suspend fun getClarificationContext(cardId: Int): ClarificationContext? {
        val card = theoryDao.getCard(cardId)?.let(mapper::toTheoryCard) ?: return null
        return ClarificationContext(
            cardId = card.id,
            cardTitle = card.title,
            microtopicTitle = theoryDao.getMicrotopicTitle(card.microtopicId).orEmpty(),
            theoryText = TheoryPlainText.render(card.blocks),
            options = card.clarificationOptions,
        )
    }

    override fun observeSearchIndex(): Flow<SearchIndex> = combine(
        theoryDao.getCategories(),
        theoryDao.getTopics(),
        theoryDao.getAllMicrotopics(),
        theoryDao.getCardTitles(),
        progressDao.getAllMicrotopicProgress(),
    ) { categories, topics, microtopics, cardTitles, progress ->
        val sectionTitles = categories.associate { it.id to it.title }
        val cardsByMicrotopic = cardTitles.groupBy { it.microtopicId }
        val completed = progress.filter { it.isCompleted }.map { it.microtopicId }.toSet()
        val microtopicsByTopic = microtopics.groupBy { it.topicId }
        SearchIndex(
            topics = topics.map { topic ->
                val own = microtopicsByTopic[topic.id].orEmpty().sortedBy { it.order }
                IndexedTopic(
                    id = topic.id,
                    title = topic.title,
                    keywords = SearchKeywords.parse(topic.searchKeywords),
                    description = topic.description,
                    sectionTitle = topic.categoryId?.let(sectionTitles::get),
                    order = topic.order,
                    completedMicrotopics = own.count { it.id in completed },
                    microtopics = own.map { microtopic ->
                        IndexedMicrotopic(
                            id = microtopic.id,
                            title = microtopic.title,
                            keywords = SearchKeywords.parse(microtopic.searchKeywords),
                            cardTitles = cardsByMicrotopic[microtopic.id].orEmpty().map { it.title },
                            order = microtopic.order,
                            isCompleted = microtopic.id in completed,
                        )
                    },
                )
            },
        )
    }

    override fun observeMicrotopicCards(microtopicId: Int): Flow<MicrotopicCards> = combine(
        theoryDao.observeMicrotopic(microtopicId),
        theoryDao.getCards(microtopicId),
        theoryDao.getCardIdsWithHardcodedExercises(microtopicId),
        theoryDao.getCardIdsWithAiExercise(microtopicId),
        progressDao.getCompletedCardIds(),
    ) { microtopic, cards, hardcodedIds, aiIds, completedCardIds ->
        val withHardcoded = hardcodedIds.toSet()
        val withAi = aiIds.toSet()
        MicrotopicCards(
            microtopicTitle = microtopic?.title.orEmpty(),
            cards = cards.map { card ->
                mapper.toTheoryCard(card).copy(
                    hasExercises = card.id in withHardcoded || card.id in withAi,
                    hasAiExercise = card.id in withAi,
                )
            },
            completedCardIds = completedCardIds.toSet(),
        )
    }
}