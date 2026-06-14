package dev.sethan8r.grammar.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Биндинги domain-интерфейсов к реализациям (через @Binds).
 * Серверные интерфейсы (AuthRepository, EntitlementsProvider, ...) и их Fake-реализации
 * добавляются в шаге D2; репозитории теории — в шаге E. Сейчас — пустой скелет.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule