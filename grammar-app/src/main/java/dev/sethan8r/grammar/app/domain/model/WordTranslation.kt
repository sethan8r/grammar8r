package dev.sethan8r.grammar.app.domain.model

/**
 * Перевод слова для попапа по долгому нажатию (`TranslatableText`).
 * Отдаётся [dev.sethan8r.grammar.app.domain.repository.DictionaryRepository].
 *
 * Источник — внешние словари (LingvoLive → Yandex) + кэш `DictionaryCache`. Это НЕ контракт нашего
 * сервера, поэтому DTO в grammar-shared не заводится — модель чисто доменная.
 *
 * `transcription == null` — словарь не дал транскрипцию. `translations` — варианты, самые ходовые
 * первыми (порядок задаёт провайдер/кэш).
 */
data class WordTranslation(
    val word: String,
    val transcription: String?,
    val translations: List<String>,
)