package dev.sethan8r.grammar.app.data.local.content.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sethan8r.grammar.app.data.local.content.dao.projection.CardTitle
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarCard
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarMicrotopic
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarTopic
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarTopicCategory
import kotlinx.coroutines.flow.Flow

/**
 * Чтение дерева теории из content.db (read-only). Запись отсутствует: content.db сидируется из assets.
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

    @Query("SELECT title FROM grammar_microtopics WHERE id = :microtopicId")
    suspend fun getMicrotopicTitle(microtopicId: Int): String?

    /** Заголовки всех карточек для поискового индекса — без тела теории (оно в индекс не входит). */
    @Query("SELECT id, microtopicId, title FROM grammar_cards")
    fun getCardTitles(): Flow<List<CardTitle>>

    /** id карточек микротемы, у которых есть хотя бы одно хардкод-упражнение. */
    @Query(
        "SELECT DISTINCT cardId FROM card_exercise_index " +
            "WHERE cardId IN (SELECT id FROM grammar_cards WHERE microtopicId = :microtopicId)"
    )
    fun getCardIdsWithHardcodedExercises(microtopicId: Int): Flow<List<Int>>

    /** id карточек микротемы, у которых есть умное (AI) задание. */
    @Query(
        "SELECT DISTINCT cardId FROM ai_exercises " +
            "WHERE cardId IN (SELECT id FROM grammar_cards WHERE microtopicId = :microtopicId)"
    )
    fun getCardIdsWithAiExercise(microtopicId: Int): Flow<List<Int>>
}