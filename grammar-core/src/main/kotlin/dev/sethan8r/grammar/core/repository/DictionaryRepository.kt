package dev.sethan8r.grammar.core.repository

import dev.sethan8r.grammar.core.model.dictionary.WordTranslation

/**
 * Перевод слова по долгому нажатию (`TranslatableText`): LingvoLive → Yandex → кэш `DictionaryCache`
 * (см. CLAUDE.md → «Перевод слов по долгому нажатию»). Внешние словари, не наш серверный контракт.
 *
 * Интерфейс закладывается с первого дня (CLAUDE.md), но сам попап/жест/провайдеры — отдельный шаг
 * позже. Фаза 1 — заглушка
 * ([dev.sethan8r.grammar.app.data.repository.fake.FakeDictionaryRepository]): возвращает null.
 */
interface DictionaryRepository {

    /** Перевод слова; `null` — перевод не найден (или фича ещё не включена). */
    suspend fun translate(word: String): WordTranslation?
}