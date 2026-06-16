package dev.sethan8r.grammar.app.domain.model.exercise

/**
 * Текущий ввод пользователя по упражнению. Хранится во ViewModel сессии (решение принимает VM,
 * UI лишь отображает и шлёт изменения). Проверяется [dev.sethan8r.grammar.app.domain.usecase.ExerciseEvaluator].
 */
sealed interface ExerciseAnswer {

    /** Выбор одного варианта в [Exercise.Choice]. [selectedIndex] = -1 — ничего не выбрано. */
    data class SingleChoice(val selectedIndex: Int = -1) : ExerciseAnswer

    /** Введённые строки по пунктам [Exercise.TextInput] (по индексу пункта). */
    data class TextAnswers(val inputs: List<String>) : ExerciseAnswer
}