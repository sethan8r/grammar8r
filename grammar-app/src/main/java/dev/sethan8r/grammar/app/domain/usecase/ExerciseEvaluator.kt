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
                exercise.items.indices.all { i -> matches(exercise.items[i], inputs[i]) }
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

        // Плашки-сегменты (нереализованный тип / умное задание) отвечать не требуют.
        is Exercise.Placeholder -> true
    }

    /**
     * Пункт TextInput засчитан, если нормализованный ввод совпал с нормализованным ответом или любой
     * альтернативой. Нормализация ([AnswerNormalizer]) гасит регистр, апострофы, форму сокращений
     * (don't = do not) и пунктуацию, поэтому полная и сокращённая записи равнозначны.
     */
    private fun matches(item: TextItem, input: String): Boolean =
        (listOf(item.answer) + item.alternatives).any { AnswerNormalizer.matches(it, input) }
}