package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Кэш переводов от LingvoLive / Yandex Dictionary (под перевод по долгому тапу).
 * `word` — в нижнем регистре (case-insensitive). `translations`/`responseJson` — JSON-`String`.
 * Источник правды по схеме — db_schema.md → `DictionaryCache`.
 */
@Entity(tableName = "dictionary_cache")
data class DictionaryCache(
    @PrimaryKey val word: String,
    val provider: String,
    val transcription: String?,
    val translations: String,
    val responseJson: String,
    val fetchedAt: Long
)