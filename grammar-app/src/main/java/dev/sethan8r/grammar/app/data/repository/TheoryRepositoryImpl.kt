package dev.sethan8r.grammar.app.data.repository

import dev.sethan8r.grammar.app.data.local.content.dao.TheoryDao
import dev.sethan8r.grammar.app.data.local.user.dao.ProgressDao
import dev.sethan8r.grammar.app.data.mapper.TheoryContentMapper
import dev.sethan8r.grammar.app.domain.model.MicrotopicCards
import dev.sethan8r.grammar.app.domain.model.MicrotopicState
import dev.sethan8r.grammar.app.domain.model.MicrotopicSummary
import dev.sethan8r.grammar.app.domain.model.TheoryCategory
import dev.sethan8r.grammar.app.domain.model.TheoryData
import dev.sethan8r.grammar.app.domain.model.TheoryMicrotopicRef
import dev.sethan8r.grammar.app.domain.model.TheoryTopic
import dev.sethan8r.grammar.app.domain.model.TopicMicrotopics
import dev.sethan8r.grammar.app.domain.repository.TheoryRepository
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

    override fun observeMicrotopicCards(microtopicId: Int): Flow<MicrotopicCards> = combine(
        theoryDao.observeMicrotopic(microtopicId),
        theoryDao.getCards(microtopicId),
        progressDao.getCompletedCardIds(),
    ) { microtopic, cards, completedCardIds ->
        MicrotopicCards(
            microtopicTitle = microtopic?.title.orEmpty(),
            cards = cards.map(mapper::toTheoryCard),
            completedCardIds = completedCardIds.toSet(),
        )
    }
}