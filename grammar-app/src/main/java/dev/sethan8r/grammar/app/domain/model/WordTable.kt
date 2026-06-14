package dev.sethan8r.grammar.app.domain.model

/**
 * Таблица-источник слова. Ровно три — фиксировано.
 *
 * Используется как дискриминатор в `UserWordProgress` (составной ключ `(source, wordId)`):
 * id в `course_words`, `irregular_verbs` и словаре Words8r нумеруются независимо, без источника
 * были бы коллизии. Также определяет, откуда [AiExerciseWordsSource] тянет слова для AI-промта.
 *
 * Источник правды — db_schema.md → ENUM `WordTable`.
 */
enum class WordTable {
    WORDS8R,          // словарь Words8r (личный пул) — таблица в content.db, перенос в Фазе 2
    COURSE_WORDS,     // слова курса Grammar8r — `course_words`
    IRREGULAR_VERBS   // неправильные глаголы курса — `irregular_verbs`
}