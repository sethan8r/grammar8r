package dev.sethan8r.grammar.app.di

import dev.sethan8r.grammar.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Конфигурация сборки через DI. Единственное место, где код обращается к `BuildConfig`, —
 * dev/prod-различие пробрасывается как зависимость (правило CLAUDE.md: dev/prod через
 * BuildConfig-поле, не «TODO раскомментировать»; getInstance/static в логике — нет).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    @DebugBuild
    fun provideDebugFlag(): Boolean = BuildConfig.DEBUG
}