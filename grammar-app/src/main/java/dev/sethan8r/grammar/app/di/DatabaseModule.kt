package dev.sethan8r.grammar.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Провайдеры Room: ContentDatabase (content.db, read-only) и UserDatabase (user.db, mutable)
 * плюс их DAO. Наполняется в шаге B (схема Room) и C (сидинг). Сейчас — пустой скелет.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule