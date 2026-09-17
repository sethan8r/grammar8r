package dev.sethan8r.grammar.app.data.local.content.entity.word

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Неправильный глагол курса — три формы + перевод (read-only). Отдельная таблица от course_words:
 * механика повторений проверяет все три формы. В AI-промт идёт как "go / went / gone".
 *
 * ⚠️ `id` стабильный (как у course_words). Состояние — user.db → `UserWordProgress`
 * (source = IRREGULAR_VERBS). Источник правды по схеме — db_schema.md → `irregular_verbs`.
 */
@Entity(
    tableName = "irregular_verbs",
    indices = [Index("microtopicId"), Index("categoryId")]
)
data class IrregularVerb(
    @PrimaryKey val id: Int,
    val v1: String,
    val v2: String,
    val v3: String,
    val translation: String,
    val transcription: String?,
    val microtopicId: Int,
    val categoryId: String
)