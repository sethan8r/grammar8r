package dev.sethan8r.grammar.app.data.local.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sethan8r.grammar.app.data.local.user.entity.DictionaryCache

/**
 * Кэш переводов в user.db (под перевод по тапу). Запись — REPLACE: свежий ответ API
 * перетирает устаревший по тому же слову. Базовый набор — расширяется при реализации перевода.
 */
@Dao
interface DictionaryCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entry: DictionaryCache)

    @Query("SELECT * FROM dictionary_cache WHERE word = :word")
    suspend fun get(word: String): DictionaryCache?
}