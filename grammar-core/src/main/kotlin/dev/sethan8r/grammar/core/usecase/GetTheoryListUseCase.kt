package dev.sethan8r.grammar.core.usecase

import dev.sethan8r.grammar.core.model.theory.TheoryData
import dev.sethan8r.grammar.core.model.theory.TheoryListItem
import dev.sethan8r.grammar.core.model.theory.TheoryTopic
import dev.sethan8r.grammar.core.model.theory.TopicSummary
import dev.sethan8r.grammar.core.repository.TheoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Строит список верхнего уровня вкладки «Учить»: темы без раздела и разделы с вложенными темами,
 * переплетённые по общему порядку (`order`). Чистая логика, тестируемая без Android.
 */
class GetTheoryListUseCase @Inject constructor(
    private val repository: TheoryRepository,
) {

    operator fun invoke(): Flow<List<TheoryListItem>> =
        repository.observeTheoryData().map(::build)

    private fun build(data: TheoryData): List<TheoryListItem> {
        // (order в общем пространстве -> элемент). Раздел и отдельная тема делят одно пространство.
        val standalone = data.topics
            .filter { it.categoryId == null }
            .map { it.order to TheoryListItem.TopicItem(data.summary(it)) }

        val topicsByCategory = data.topics
            .filter { it.categoryId != null }
            .groupBy { it.categoryId!! }

        val sections = data.categories.map { category ->
            val topics = topicsByCategory[category.id].orEmpty()
                .sortedBy { it.order }
                .map { data.summary(it) }
            category.order to TheoryListItem.SectionItem(
                id = category.id,
                title = category.title,
                description = category.description,
                topics = topics,
            )
        }

        return (standalone + sections)
            .sortedBy { it.first }
            .map { it.second }
    }

    private fun TheoryData.summary(topic: TheoryTopic): TopicSummary {
        val microtopics = microtopicsByTopic[topic.id].orEmpty()
        return TopicSummary(
            id = topic.id,
            title = topic.title,
            description = topic.description,
            isPretopic = topic.isPretopic,
            completedMicrotopics = microtopics.count { it.id in completedMicrotopicIds },
            totalMicrotopics = microtopics.size,
        )
    }
}