package dev.sethan8r.grammar.app.di

import dev.sethan8r.grammar.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Конфигурация сборки через DI: единственное место, где код обращается к `BuildConfig`.
 * dev/prod-различие пробрасывается как зависимость, а не через static/getInstance в логике.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    @DebugBuild
    fun provideDebugFlag(): Boolean = BuildConfig.DEBUG
}