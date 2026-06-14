package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import dev.sethan8r.grammar.app.domain.model.WordTable

/**
 * Состояние изучения слова — единый источник правды для всех трёх словарей (course/irregular/words8r).
 * Создаётся лениво при открытии слова; нет строки = слово закрыто.
 *
 * Составной ключ `(source, wordId)`: id в таблицах-источниках нумеруются независимо, без `source`
 * были бы коллизии. Между БД нет FK — `wordId` указывает на content.db по значению, осиротевшее
 * состояние просто игнорируется. Источник правды по схеме — db_schema.md → `UserWordProgress`.
 */
@Entity(
    tableName = "user_word_progress",
    primaryKeys = ["source", "wordId"]
)
data class UserWordProgress(
    val source: WordTable,
    val wordId: Int,
    val isUnlocked: Boolean,
    val isHidden: Boolean,
    val qRep: Int,
    val isPriority: Boolean
)