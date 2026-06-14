package dev.sethan8r.grammar.app.data.local.content.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarCard
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarMicrotopic
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarTopic
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarTopicCategory
import kotlinx.coroutines.flow.Flow

/**
 * Чтение дерева теории из content.db (read-only). Базовый набор запросов — расширяется в Шаге E
 * (читалка) по мере появления экранов. Запись отсутствует: content.db сидируется из assets.
 */
@Dao
interface TheoryDao {

    @Query("SELECT * FROM grammar_topic_categories ORDER BY `order`")
    fun getCategories(): Flow<List<GrammarTopicCategory>>

    @Query("SELECT * FROM grammar_topics ORDER BY `order`")
    fun getTopics(): Flow<List<GrammarTopic>>

    @Query("SELECT * FROM grammar_microtopics ORDER BY `order`")
    fun getAllMicrotopics(): Flow<List<GrammarMicrotopic>>

    @Query("SELECT * FROM grammar_microtopics WHERE topicId = :topicId ORDER BY `order`")
    fun getMicrotopics(topicId: Int): Flow<List<GrammarMicrotopic>>

    @Query("SELECT * FROM grammar_microtopics WHERE id = :microtopicId")
    fun observeMicrotopic(microtopicId: Int): Flow<GrammarMicrotopic?>

    @Query("SELECT * FROM grammar_cards WHERE microtopicId = :microtopicId ORDER BY `order`")
    fun getCards(microtopicId: Int): Flow<List<GrammarCard>>

    @Query("SELECT * FROM grammar_cards WHERE id = :cardId")
    suspend fun getCard(cardId: Int): GrammarCard?
}