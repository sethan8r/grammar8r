package dev.sethan8r.grammar.app.data.local.content.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sethan8r.grammar.app.data.local.content.entity.word.CourseCategory
import dev.sethan8r.grammar.app.data.local.content.entity.word.CourseWord
import dev.sethan8r.grammar.app.data.local.content.entity.word.CourseWordGroup
import dev.sethan8r.grammar.app.data.local.content.entity.word.IrregularVerb
import kotlinx.coroutines.flow.Flow

/**
 * Чтение слов курса из content.db (read-only). Ключ выборки для BottomSheet открытия слов —
 * `microtopicId`. Базовый набор — расширяется при реализации открытия слов / Словаря.
 */
@Dao
interface CourseWordDao {

    @Query("SELECT * FROM course_word_groups ORDER BY `order`")
    fun getGroups(): Flow<List<CourseWordGroup>>

    @Query("SELECT * FROM course_categories ORDER BY `order`")
    fun getCategories(): Flow<List<CourseCategory>>

    @Query("SELECT * FROM course_words WHERE microtopicId = :microtopicId")
    suspend fun getCourseWordsByMicrotopic(microtopicId: Int): List<CourseWord>

    @Query("SELECT * FROM irregular_verbs WHERE microtopicId = :microtopicId")
    suspend fun getIrregularVerbsByMicrotopic(microtopicId: Int): List<IrregularVerb>
}