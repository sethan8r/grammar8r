package dev.sethan8r.grammar.app.domain.model.exercise

/**
 * Один источник слов для AI-промта: таблица + категория + сколько взять.
 *
 * `categoryId == null` — вся таблица без фильтра по категории. Тип строковый (стабильные
 * текстовые ID категорий, напр. "basic_verbs"), общий для обоих пулов слов.
 */
data class WordSource(
    val table: WordTable,
    val categoryId: String?,
    val targetCount: Int
)

/**
 * Откуда брать слова для AI-промта перед стартом упражнения. Хранится в `AiExercise.wordsSource`.
 *
 * Доменная сущность определяется здесь один раз вместе со всеми атрибутами (источники, минимум
 * слов, человекочитаемое имя) — выборку строит `WordsRepository` итерацией по `sources`.
 * Источник правды по составу/семантике — db_schema.md → ENUM `AiExerciseWordsSource`.
 *
 * ⚠️ `displayName` пока живёт здесь как доменный атрибут; при появлении экрана блокировки
 * упражнения текст уедет в strings.xml (помечено в decision_log Шага B).
 */
enum class AiExerciseWordsSource(
    val sources: List<WordSource>,
    val minWords: Int,
    val displayName: String
) {
    /** Упражнение не использует слова вообще (например, AI-уточнение «Не совсем понял»). */
    NONE(
        sources = emptyList(),
        minWords = 0,
        displayName = ""
    ),

    /** Общий словарь: 70 слов из Words8r + 30 из course_words, без фильтра по категории. */
    GENERAL(
        sources = listOf(
            WordSource(WordTable.WORDS8R, null, 70),
            WordSource(WordTable.COURSE_WORDS, null, 30)
        ),
        minWords = 10,
        displayName = "общий словарь"
    ),

    /** Только слова категории «Informal English» из Words8r. */
    INFORMAL_ENGLISH(
        sources = listOf(
            WordSource(WordTable.WORDS8R, "informal_english", 30)
        ),
        minWords = 5,
        displayName = "Разговорный английский"
    ),

    /** Неправильные глаголы (v1/v2/v3) из Grammar8r. */
    VERB_FORMS(
        sources = listOf(
            WordSource(WordTable.IRREGULAR_VERBS, null, 50)
        ),
        minWords = 10,
        displayName = "Формы глаголов"
    )
}