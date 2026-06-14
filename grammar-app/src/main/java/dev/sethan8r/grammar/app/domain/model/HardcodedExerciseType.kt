package dev.sethan8r.grammar.app.domain.model

/**
 * Тип хардкодного упражнения. Хранится в `CardExerciseIndex.exerciseType` и определяет, в какой
 * таблице content.db искать само упражнение по `CardExerciseIndex.exerciseId`.
 *
 * Источник правды — db_schema.md → ENUM `HardcodedExerciseType`. Три CHOICE-подтипа лежат в одной
 * таблице `multiple_choice_exercises` (различает [ChoiceType]); остальные типы — по своей таблице.
 */
enum class HardcodedExerciseType {
    WORD_ARRANGEMENT,       // перемешанные слова → собрать предложение
    MULTIPLE_CHOICE,        // пропуск → выбрать из 2–4 вариантов
    FORWARD_CHOICE,         // RU предложение → выбрать правильный EN вариант
    REVERSE_CHOICE,         // EN предложение → выбрать правильный RU перевод
    TEXT_INPUT,             // пропуск → вписать ответ вручную
    MATCHING,               // пары RU↔EN → соединить линиями
    TRUE_FALSE,             // 5 предложений → отметить верно/неверно
    ERROR_CORRECTION,       // сломанное предложение → выбрать правильный вариант
    TRANSFORMATION,         // утверждение → трансформировать по правилу
    CATEGORIZATION,         // слова → перетащить в колонки-категории
    TABLE_FILL,             // таблица с пропусками → вписать формы
    FIND_THE_ODD,           // 4 элемента → найти лишний по правилу
    CONSTRUCTION_MEANING,   // грамматическая конструкция → выбрать правильный RU смысл
    DIALOG_RESTORE          // диалог с пропуском → выбрать правильную реплику
}