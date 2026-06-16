package dev.sethan8r.grammar.app.data.local.user

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sethan8r.grammar.app.data.local.converter.Converters
import dev.sethan8r.grammar.app.data.local.user.dao.DictionaryCacheDao
import dev.sethan8r.grammar.app.data.local.user.dao.ProgressDao
import dev.sethan8r.grammar.app.data.local.user.dao.StatsDao
import dev.sethan8r.grammar.app.data.local.user.dao.WordProgressDao
import dev.sethan8r.grammar.app.data.local.user.entity.AiRequestCounter
import dev.sethan8r.grammar.app.data.local.user.entity.DailyStats
import dev.sethan8r.grammar.app.data.local.user.entity.DictionaryCache
import dev.sethan8r.grammar.app.data.local.user.entity.FavoriteAiExercise
import dev.sethan8r.grammar.app.data.local.user.entity.UserAiExerciseStats
import dev.sethan8r.grammar.app.data.local.user.entity.UserCardProgress
import dev.sethan8r.grammar.app.data.local.user.entity.UserCategorySettings
import dev.sethan8r.grammar.app.data.local.user.entity.UserExerciseResult
import dev.sethan8r.grammar.app.data.local.user.entity.UserMicrotopicProgress
import dev.sethan8r.grammar.app.data.local.user.entity.UserWordProgress

/**
 * user.db — mutable БД прогресса. Создаётся на телефоне и НИКОГДА не перезаписывается при
 * обновлении приложения. Только честные миграции с первого дня; destructive fallback ЗАПРЕЩЁН
 * (теряется прогресс пользователя). Контент живёт отдельно в content.db; JOIN/FK между БД нет —
 * «контент + прогресс» склеивается в репозитории через combine().
 *
 * Источник правды по схеме — db_schema.md.
 * 🟡 provisional (Шаг B) — решения зафиксированы в tasks/foundation/decision_log.md.
 */
@Database(
    entities = [
        UserCardProgress::class,
        UserMicrotopicProgress::class,
        UserExerciseResult::class,
        UserAiExerciseStats::class,
        FavoriteAiExercise::class,
        UserWordProgress::class,
        UserCategorySettings::class,
        DailyStats::class,
        AiRequestCounter::class,
        DictionaryCache::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun wordProgressDao(): WordProgressDao
    abstract fun statsDao(): StatsDao
    abstract fun dictionaryCacheDao(): DictionaryCacheDao
}