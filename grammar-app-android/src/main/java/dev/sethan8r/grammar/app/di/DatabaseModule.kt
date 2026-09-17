package dev.sethan8r.grammar.app.di

import android.content.Context
import androidx.room.Room
import dev.sethan8r.grammar.app.data.local.content.ContentDatabase
import dev.sethan8r.grammar.app.data.local.content.dao.CourseWordDao
import dev.sethan8r.grammar.app.data.local.content.dao.ExerciseDao
import dev.sethan8r.grammar.app.data.local.content.dao.TheoryDao
import dev.sethan8r.grammar.app.data.local.user.UserDatabase
import dev.sethan8r.grammar.app.data.local.user.dao.DictionaryCacheDao
import dev.sethan8r.grammar.app.data.local.user.dao.ProgressDao
import dev.sethan8r.grammar.app.data.local.user.dao.StatsDao
import dev.sethan8r.grammar.app.data.local.user.dao.WordProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

/**
 * Провайдеры Room: ContentDatabase (content.db, read-only) и UserDatabase (user.db, mutable) + их DAO.
 *
 * content.db: открывается из assets через `createFromAsset`; destructive fallback разрешён ТОЛЬКО
 * здесь (БД заменяется целиком при обновлении приложения, пользовательских данных нет). Открытие
 * у Room ленивое — до первого обращения к DAO файл не вскрывается.
 *
 * user.db: обычная databaseBuilder, БЕЗ destructive fallback (миграции честные, прогресс беречь).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideContentDatabase(
        @ApplicationContext context: Context,
        @DebugBuild debug: Boolean,
    ): ContentDatabase {
        // Dev-обкатка контента: content.db часто пересобирается, но `createFromAsset` копирует ассет
        // лишь при первом создании файла — без смены версии новый контент на устройство не попадает.
        // В debug удаляем старый файл, чтобы Room всегда копировал свежий ассет. В release не трогаем
        // (БД read-only, заменяется штатно при апдейте версии content.db).
        if (debug) {
            context.getDatabasePath("content.db").let { file ->
                file.delete()
                File("${file.path}-wal").delete()
                File("${file.path}-shm").delete()
            }
        }
        return Room.databaseBuilder(context, ContentDatabase::class.java, "content.db")
            .createFromAsset("content.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase =
        Room.databaseBuilder(context, UserDatabase::class.java, "user.db")
            .build()

    // --- content.db DAO ---

    @Provides
    fun provideTheoryDao(db: ContentDatabase): TheoryDao = db.theoryDao()

    @Provides
    fun provideExerciseDao(db: ContentDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideCourseWordDao(db: ContentDatabase): CourseWordDao = db.courseWordDao()

    // --- user.db DAO ---

    @Provides
    fun provideProgressDao(db: UserDatabase): ProgressDao = db.progressDao()

    @Provides
    fun provideWordProgressDao(db: UserDatabase): WordProgressDao = db.wordProgressDao()

    @Provides
    fun provideStatsDao(db: UserDatabase): StatsDao = db.statsDao()

    @Provides
    fun provideDictionaryCacheDao(db: UserDatabase): DictionaryCacheDao = db.dictionaryCacheDao()
}