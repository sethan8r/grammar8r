package dev.sethan8r.grammar.core.model.exercise

/**
 * Подтип упражнения с выбором варианта внутри таблицы `multiple_choice_exercises`.
 * UI рендерит все три одинаково, отличается только подача условия (`prompt`).
 *
 * Источник правды — db_schema.md / exercise_templates.md → `MultipleChoiceExercise`.
 */
enum class ChoiceType {
    CHOICE,          // EN предложение с пропуском → вставить слово
    FORWARD_CHOICE,  // RU предложение/ситуация → выбрать правильный EN вариант
    REVERSE_CHOICE   // EN предложение → выбрать правильный RU перевод
}