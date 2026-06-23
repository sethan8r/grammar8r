package dev.sethan8r.grammar.app.di

import dev.sethan8r.grammar.app.data.repository.fake.FakeAiExerciseRepository
import dev.sethan8r.grammar.app.data.repository.fake.FakeAuthRepository
import dev.sethan8r.grammar.app.data.repository.fake.FakeDictionaryRepository
import dev.sethan8r.grammar.app.data.repository.fake.FakeEntitlementsProvider
import dev.sethan8r.grammar.app.data.repository.ExerciseRepositoryImpl
import dev.sethan8r.grammar.app.data.repository.ProgressRepositoryImpl
import dev.sethan8r.grammar.app.data.repository.TheoryRepositoryImpl
import dev.sethan8r.grammar.app.data.repository.fake.FakeProgressSyncRepository
import dev.sethan8r.grammar.app.domain.repository.AiExerciseRepository
import dev.sethan8r.grammar.app.domain.repository.AuthRepository
import dev.sethan8r.grammar.app.domain.repository.DictionaryRepository
import dev.sethan8r.grammar.app.domain.repository.EntitlementsProvider
import dev.sethan8r.grammar.app.domain.repository.ExerciseRepository
import dev.sethan8r.grammar.app.domain.repository.ProgressRepository
import dev.sethan8r.grammar.app.domain.repository.ProgressSyncRepository
import dev.sethan8r.grammar.app.domain.repository.TheoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Биндинги domain-интерфейсов к реализациям (@Binds). Серверные интерфейсы пока привязаны к
 * Fake-заглушкам ([dev.sethan8r.grammar.app.data.repository.fake]); при появлении реальных
 * реализаций (Фаза 4) меняется только правая часть биндинга — потребители не трогаются.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FakeAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindEntitlementsProvider(impl: FakeEntitlementsProvider): EntitlementsProvider

    @Binds
    @Singleton
    abstract fun bindAiExerciseRepository(impl: FakeAiExerciseRepository): AiExerciseRepository

    @Binds
    @Singleton
    abstract fun bindDictionaryRepository(impl: FakeDictionaryRepository): DictionaryRepository

    @Binds
    @Singleton
    abstract fun bindProgressSyncRepository(impl: FakeProgressSyncRepository): ProgressSyncRepository

    /** Репозиторий теории: combine content.db + user.db. */
    @Binds
    @Singleton
    abstract fun bindTheoryRepository(impl: TheoryRepositoryImpl): TheoryRepository

    /** Упражнения карточки из content.db. */
    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    /** Единая точка записи прогресса прохождения: карточка/микротема + счёт. */
    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository
}