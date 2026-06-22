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

    /**
     * Набор выбранных индексов в [Exercise.TrueFalse] (multi-select «отметь верные»). Верно, когда
     * совпадает с множеством истинных утверждений.
     */
    data class MultiChoice(val selectedIndices: Set<Int> = emptySet()) : ExerciseAnswer

    /**
     * Текущий порядок правой колонки [Exercise.Matching] — тексты правых элементов сверху вниз. Левая
     * колонка закреплена; верно, когда правый текст в каждой позиции совпадает с парным левым.
     */
    data class Pairing(val rightOrder: List<String>) : ExerciseAnswer

    /**
     * Раскладка элементов [Exercise.Categorization] по колонкам: «текст элемента → индекс категории».
     * Элемент в пуле (ещё не разложен) в карте отсутствует. Верно, когда каждый элемент в своей категории.
     */
    data class Buckets(val placement: Map<String, Int>) : ExerciseAnswer
}