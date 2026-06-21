package dev.sethan8r.grammar.app.domain.model.exercise

/**
 * Текущий ввод пользователя по упражнению. Хранится во ViewModel сессии (решение принимает VM,
 * UI лишь отображает и шлёт изменения). Проверяется [dev.sethan8r.grammar.app.domain.usecase.ExerciseEvaluator].
 */
sealed interface ExerciseAnswer {

    /** Выбор одного варианта в [Exercise.Choice]. [selectedIndex] = -1 — ничего не выбрано. */
    data class SingleChoice(val selectedIndex: Int = -1) : ExerciseAnswer

    /**
     * Введённые строки по пунктам/ячейкам (по индексу). Общий для [Exercise.TextInput],
     * [Exercise.TableFill] (строка на ячейку) и [Exercise.Transformation] (строка на пример) — все
     * три проверяются вводом текста через [dev.sethan8r.grammar.app.domain.usecase.AnswerNormalizer].
     */
    data class TextAnswers(val inputs: List<String>) : ExerciseAnswer

    /** Собранная последовательность слов в [Exercise.WordArrangement] — тексты выбранных чипов по порядку. */
    data class WordOrder(val tokens: List<String>) : ExerciseAnswer
}