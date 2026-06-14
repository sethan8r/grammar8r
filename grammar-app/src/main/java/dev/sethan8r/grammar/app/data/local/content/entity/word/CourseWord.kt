package dev.sethan8r.grammar.app.data.local.content.entity.word

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Слово курса — определение (read-only). Состояние изучения (`isUnlocked`/`isHidden`/`qRep`/
 * `isPriority`) живёт в user.db → `UserWordProgress` (source = COURSE_WORDS).
 *
 * ⚠️ `id` обязан быть СТАБИЛЬНЫМ между пересборками content.db — иначе осиротеет прогресс
 * в user.db. Источник правды по схеме — db_schema.md → `course_words`.
 */
@Entity(
    tableName = "course_words",
    indices = [Index("microtopicId"), Index("categoryId")]
)
data class CourseWord(
    @PrimaryKey val id: Int,
    val word: String,
    val translation: String,
    val transcription: String?,
    val microtopicId: Int,
    val categoryId: String
)