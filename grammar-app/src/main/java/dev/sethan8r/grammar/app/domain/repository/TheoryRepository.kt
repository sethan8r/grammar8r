package dev.sethan8r.grammar.app.domain.repository

import dev.sethan8r.grammar.app.domain.model.MicrotopicCards
import dev.sethan8r.grammar.app.domain.model.TheoryData
import dev.sethan8r.grammar.app.domain.model.TopicMicrotopics
import kotlinx.coroutines.flow.Flow

/**
 * Доступ к дереву теории. Реализация объединяет content.db (теория, read-only) и user.db (прогресс)
 * и разбирает сырой JSON карточек в типизированные модели. Между БД нет JOIN/FK — склейка идёт
 * через `combine` потоков (db_schema.md → «Три обязательных решения»).
 */
interface TheoryRepository {

    /** Сырой снимок для списка теории (темы + разделы + микротемы + прогресс). */
    fun observeTheoryData(): Flow<TheoryData>

    /** Микротемы темы с прогрессом + заголовок темы. */
    fun observeTopicMicrotopics(topicId: Int): Flow<TopicMicrotopics>

    /** Карточки микротемы (теория разобрана в блоки) + заголовок микротемы. */
    fun observeMicrotopicCards(microtopicId: Int): Flow<MicrotopicCards>
}