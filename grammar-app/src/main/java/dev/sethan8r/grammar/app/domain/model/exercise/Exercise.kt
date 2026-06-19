package dev.sethan8r.grammar.app.domain.model.exercise

/**
 * Доменное упражнение карточки — то, что показывает движок (Шаг F). Получается из Entity content.db
 * маппером ([dev.sethan8r.grammar.app.data.mapper.ExerciseContentMapper]): сырой JSON полей
 * (`options`/`items`) уже разобран в типизированные списки.
 *
 * [Placeholder]-сегменты (нереализованный тип / умное задание) проходятся кнопкой «Далее», в счёт
 * «верно X из N» не идут — но считаются делением полосы прогресса.
 */
sealed interface Exercise {
    val id: Int

    /** Сегмент-заглушка: отвечать нечего, «Далее» сразу активна, в счёт не идёт. */
    sealed interface Placeholder : Exercise

    /**
     * Упражнения «выбери один вариант из списка, ровно один правильный». Общая механика для всех них —
     * одна ([ExerciseAnswer.SingleChoice] + [dev.sethan8r.grammar.app.domain.usecase.ExerciseEvaluator] +
     * рендерер `SingleSelectExerciseView`); типы отличаются ТОЛЬКО подачей условия (шапкой). Поэтому
     * движок работает с этим зонтиком, а не с каждым типом по отдельности (Правило №0).
     */
    sealed interface SingleSelect : Exercise {
        val options: List<Option>
        val explanation: String
        val type: HardcodedExerciseType
    }

    /**
     * Выбор варианта: `MULTIPLE_CHOICE` / `FORWARD_CHOICE` / `REVERSE_CHOICE` (различает [choiceType]).
     * Все три лежат в одной таблице и рендерятся одинаково — отличается лишь подача [prompt]/[contextRu].
     */
    data class Choice(
        override val id: Int,
        val choiceType: ChoiceType,
        val prompt: String,
        val contextRu: String,
        override val options: List<Option>,
        override val explanation: String,
    ) : SingleSelect {
        override val type: HardcodedExerciseType
            get() = when (choiceType) {
                ChoiceType.CHOICE -> HardcodedExerciseType.MULTIPLE_CHOICE
                ChoiceType.FORWARD_CHOICE -> HardcodedExerciseType.FORWARD_CHOICE
                ChoiceType.REVERSE_CHOICE -> HardcodedExerciseType.REVERSE_CHOICE
            }
    }

    /** ERROR_CORRECTION — сломанное EN-предложение [wrongSentence] (без RU), выбрать правильный вариант. */
    data class ErrorCorrection(
        override val id: Int,
        val wrongSentence: String,
        override val options: List<Option>,
        override val explanation: String,
    ) : SingleSelect {
        override val type: HardcodedExerciseType get() = HardcodedExerciseType.ERROR_CORRECTION
    }

    /** CONSTRUCTION_MEANING — EN-конструкция [construction], выбрать правильный русский смысл (из 4). */
    data class ConstructionMeaning(
        override val id: Int,
        val construction: String,
        override val options: List<Option>,
        override val explanation: String,
    ) : SingleSelect {
        override val type: HardcodedExerciseType get() = HardcodedExerciseType.CONSTRUCTION_MEANING
    }

    /** DIALOG_RESTORE — диалог [lines] (ровно одна реплика — пропуск), выбрать правильную реплику. */
    data class DialogRestore(
        override val id: Int,
        val lines: List<DialogLine>,
        override val options: List<Option>,
        override val explanation: String,
    ) : SingleSelect {
        override val type: HardcodedExerciseType get() = HardcodedExerciseType.DIALOG_RESTORE
    }

    /**
     * FIND_THE_ODD — 4 элемента, выбрать лишний по правилу [groupDescription]. Элементы ложатся в общие
     * [options] (лишний → `isCorrect = true`), поэтому работает на той же механике выбора варианта.
     */
    data class FindTheOdd(
        override val id: Int,
        val groupDescription: String,
        override val options: List<Option>,
        override val explanation: String,
    ) : SingleSelect {
        override val type: HardcodedExerciseType get() = HardcodedExerciseType.FIND_THE_ODD
    }

    /** Ввод ответа вручную (case-insensitive), 1–5 пунктов в одном блоке. */
    data class TextInput(
        override val id: Int,
        val items: List<TextItem>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType get() = HardcodedExerciseType.TEXT_INPUT
    }

    /** Тип, который движок ещё не реализовал (появится в F2–F4) — показывается плашка «в разработке». */
    data class Unsupported(
        override val id: Int,
        val type: HardcodedExerciseType,
    ) : Placeholder

    /**
     * Умное (AI) задание карточки — Фаза 3. Сейчас плашка-заглушка. Несёт свой строковый
     * [exerciseId] (он длинный, в маленький бокс не влезает — показывается в подписи под полосой).
     */
    data class AiPlaceholder(
        val exerciseId: String,
    ) : Placeholder {
        override val id: Int get() = 0
    }
}

/** Один вариант ответа в [Exercise.SingleSelect]. Минимум один помечен [isCorrect]. */
data class Option(val text: String, val isCorrect: Boolean)

/** Одна реплика диалога в [Exercise.DialogRestore]. [text] = null — это пропуск (его и восстанавливают). */
data class DialogLine(val speaker: String, val text: String?)

/** Один пункт [Exercise.TextInput]: предложение с пропуском, контекст и принимаемые ответы. */
data class TextItem(
    val sentence: String,
    val contextRu: String,
    val answer: String,
    val alternatives: List<String>,
)
