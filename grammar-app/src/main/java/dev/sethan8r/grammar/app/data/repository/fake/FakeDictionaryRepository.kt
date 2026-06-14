package dev.sethan8r.grammar.app.data.repository.fake

import dev.sethan8r.grammar.app.domain.model.dictionary.WordTranslation
import dev.sethan8r.grammar.app.domain.repository.DictionaryRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заглушка словаря (Фаза 1): перевода нет — возвращает null, как при «слово не найдено».
 * Под неё уже рендерится контентный текст через `TranslatableText` (жест/попап выключены).
 * Реальные провайдеры (LingvoLive → Yandex → кэш) — отдельный шаг позже.
 */
@Singleton
class FakeDictionaryRepository @Inject constructor() : DictionaryRepository {

    override suspend fun translate(word: String): WordTranslation? = null
}