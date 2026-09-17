package dev.sethan8r.grammar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Единый [Json] для разбора контента (блоки теории и т.п.). `ignoreUnknownKeys` — чтобы добавление
 * поля в сид не роняло старые сборки; дискриминатор полиморфизма — `type` по умолчанию (совпадает
 * с тем, что пишет конвертер).
 */
@Module
@InstallIn(SingletonComponent::class)
object SerializationModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}