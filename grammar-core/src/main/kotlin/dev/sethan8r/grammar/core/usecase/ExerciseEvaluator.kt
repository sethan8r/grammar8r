package dev.sethan8r.grammar.app.domain.usecase

import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.TextItem

/**
 * Проверка ответа на упражнение — чистая доменная логика, тестируемая без Android. Движок (UI)
 * собирает ввод в [ExerciseAnswer] и спрашивает здесь «верно/неверно», не зная правил конкретного типа.
 */
object ExerciseEvaluator {

    fun isCorrect(exercise: Exercise, answer: ExerciseAnswer?): Boolean = when (exercise) {
        // Все типы с выбором варианта: верно, если выбранный вариант помечен правильным.
        is Exercise.SingleSelect -> {
            val picked = (answer as? ExerciseAnswer.SingleChoice)?.selectedIndex ?: -1
            exercise.options.getOrNull(picked)?.isCorrect == true
        }

        is Exercise.TextInput -> {
            val inputs = (answer as? ExerciseAnswer.TextAnswers)?.inputs
            inputs != null &&
                inputs.size == exercise.items.size &&
                exercise.items.indices.all { i -> matchesTextItem(exercise.items[i], inputs[i]) }
        }

        // TABLE_FILL — все ячейки верны разом (вердикт all-or-nothing на уровне экрана).
        is Exercise.TableFill -> {
            val inputs = (answer as? ExerciseAnswer.TextAnswers)?.inputs
            inputs != null &&
                inputs.size == exercise.rows.size &&
                exercise.rows.indices.all { i -> AnswerNormalizer.matches(exercise.rows[i].answer, inputs[i]) }
        }

        // TRANSFORMATION — все 3 примера верны разом.
        is Exercise.Transformation -> {
            val inputs = (answer as? ExerciseAnswer.TextAnswers)?.inputs
            inputs != null &&
                inputs.size == exercise.items.size &&
                exercise.items.indices.all { i -> AnswerNormalizer.matches(exercise.items[i].transformed, inputs[i]) }
        }

        // WORD_ARRANGEMENT — собранное предложение совпадает с эталоном (нормализация: регистр/пунктуация/пробелы).
        is Exercise.WordArrangement -> {
            val tokens = (answer as? ExerciseAnswer.WordOrder)?.tokens
            tokens != null && AnswerNormalizer.matches(exercise.correctSentence, tokens.joinToString(" "))
        }

        // TRUE_FALSE — выбраны РОВНО все истинные утверждения (ни одного ложного, ни одного пропуска).
        is Exercise.TrueFalse -> {
            val picked = (answer as? ExerciseAnswer.MultiChoice)?.selectedIndices ?: emptySet()
            val truthful = exercise.statements.indices.filter { exercise.statements[it].isTrue }.toSet()
            picked == truthful
        }

        // MATCHING — против каждого левого стоит его правое (позиционная сверка текстов правой колонки).
        is Exercise.Matching -> {
            val order = (answer as? ExerciseAnswer.Pairing)?.rightOrder
            order != null &&
                order.size == exercise.pairs.size &&
                exercise.pairs.indices.all { i -> order[i] == exercise.pairs[i].right }
        }

        // CATEGORIZATION — каждый элемент разложен в свою категорию (вердикт all-or-nothing).
        is Exercise.Categorization -> {
            val placement = (answer as? ExerciseAnswer.Buckets)?.placement ?: emptyMap()
            exercise.categories.withIndex().all { (col, category) ->
                category.items.all { item -> placement[item] == col }
            }
        }

        // Плашки-сегменты (нереализованный тип / умное задание) отвечать не требуют.
        is Exercise.Placeholder -> true
    }

    /**
     * Пункт TextInput засчитан, если нормализованный ввод совпал с нормализованным ответом или любой
     * альтернативой. Нормализация ([AnswerNormalizer]) гасит регистр, апострофы, форму сокращений
     * (don't = do not) и пунктуацию, поэтому полная и сокращённая записи равнозначны.
     *
     * Публичный, потому что рендерер задания красит на реванше КАЖДОЕ поле по отдельности (вердикт
     * задания при этом остаётся all-or-nothing) — правило «пункт верен» живёт здесь одно (Правило №0).
     */
    fun matchesTextItem(item: TextItem, input: String): Boolean =
        (listOf(item.answer) + item.alternatives).any { AnswerNormalizer.matches(it, input) }
}