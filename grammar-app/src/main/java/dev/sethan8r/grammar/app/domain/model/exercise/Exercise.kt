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
     * Выбор варианта: `MULTIPLE_CHOICE` / `FORWARD_CHOICE` / `REVERSE_CHOICE` (различает [choiceType]).
     * Все три лежат в одной таблице и рендерятся одинаково — отличается лишь подача [prompt]/[contextRu].
     */
    data class Choice(
        override val id: Int,
        val choiceType: ChoiceType,
        val prompt: String,
        val contextRu: String,
        val options: List<Option>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType
            get() = when (choiceType) {
                ChoiceType.CHOICE -> HardcodedExerciseType.MULTIPLE_CHOICE
                ChoiceType.FORWARD_CHOICE -> HardcodedExerciseType.FORWARD_CHOICE
                ChoiceType.REVERSE_CHOICE -> HardcodedExerciseType.REVERSE_CHOICE
            }
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

/** Один вариант ответа в [Exercise.Choice]. Ровно один (для F1-типов) помечен [isCorrect]. */
data class Option(val text: String, val isCorrect: Boolean)

/** Один пункт [Exercise.TextInput]: предложение с пропуском, контекст и принимаемые ответы. */
data class TextItem(
    val sentence: String,
    val contextRu: String,
    val answer: String,
    val alternatives: List<String>,
)
