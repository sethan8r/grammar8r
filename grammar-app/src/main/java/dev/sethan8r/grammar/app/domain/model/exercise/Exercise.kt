package dev.sethan8r.grammar.app.domain.model.exercise

/**
 * Доменное упражнение карточки — то, что показывает движок. Получается из Entity content.db
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

    /** TABLE_FILL — таблица «подсказка → ответ» (до 7 строк), вписать ответ в каждую строку. */
    data class TableFill(
        override val id: Int,
        val taskDescription: String,
        val rows: List<TableFillRow>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType get() = HardcodedExerciseType.TABLE_FILL
    }

    /** TRANSFORMATION — одна трансформация на ровно 3 примера: дано [TransformItem.original] → вписать преобразованное. */
    data class Transformation(
        override val id: Int,
        val taskDescription: String,
        val items: List<TransformItem>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType get() = HardcodedExerciseType.TRANSFORMATION
    }

    /**
     * WORD_ARRANGEMENT — собрать предложение из чипов (слова + дистракторы-ловушки), перетаскиванием.
     * Сборка сравнивается с [correctSentence] через [AnswerNormalizer]. Банк (что показать) = [words] +
     * [distractors]; перемешивание — на стороне рендерера (презентационная случайность).
     */
    data class WordArrangement(
        override val id: Int,
        val situationRu: String,
        val correctSentence: String,
        val words: List<WordToken>,
        val distractors: List<WordToken>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType get() = HardcodedExerciseType.WORD_ARRANGEMENT
    }

    /**
     * TRUE_FALSE — ровно 5 утверждений, отметить ВСЕ верные (multi-select «отметь верные», НЕ тоггл
     * ✓/✗). Ответ — набор выбранных индексов ([ExerciseAnswer.MultiChoice]); верно, если выбраны ровно
     * все [Statement.isTrue] (ни одного ложного, ни одного пропуска). Утверждение — EN + RU-перевод.
     */
    data class TrueFalse(
        override val id: Int,
        val statements: List<Statement>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType get() = HardcodedExerciseType.TRUE_FALSE
    }

    /**
     * MATCHING — 4–6 пар «левое ↔ правое». Левая колонка закреплена (порядок [pairs]); правую
     * пользователь переставляет вертикальным drag-reorder. Верно, если против каждого левого стоит его
     * правое ([ExerciseAnswer.Pairing] — порядок правых текстов сверяется позиционно). Перемешивание
     * правой колонки — презентационная случайность рендерера (как банк в [WordArrangement]).
     */
    data class Matching(
        override val id: Int,
        val taskDescription: String,
        val pairs: List<MatchPair>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType get() = HardcodedExerciseType.MATCHING
    }

    /**
     * CATEGORIZATION — распределить 6–15 элементов по 2–3 колонкам-категориям перетаскиванием
     * (пул ↔ колонки, туда-обратно). Верно, если каждый элемент лежит в своей категории
     * ([ExerciseAnswer.Buckets] — карта «элемент → индекс колонки»). Пул и порядок — состояние рендерера.
     */
    data class Categorization(
        override val id: Int,
        val taskDescription: String,
        val categories: List<Category>,
        val explanation: String,
    ) : Exercise {
        val type: HardcodedExerciseType get() = HardcodedExerciseType.CATEGORIZATION
    }

    /**
     * Упражнение есть в индексе карточки, но его строки нет в таблице своего типа (осиротевший
     * индекс — рассинхрон content.db / баг конвейера). Аварийный fallback: показываем плашку
     * с типом и [id] вместо краша. В корректных данных не возникает.
     */
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

/** Одна строка [Exercise.TableFill]: подсказка слева, правильный ответ для поля справа. */
data class TableFillRow(val hint: String, val answer: String)

/** Один пример [Exercise.Transformation]: исходное предложение и его правильная трансформация. */
data class TransformItem(val original: String, val transformed: String)

/** Слово-чип в [Exercise.WordArrangement]: текст + перевод (пустой — слово уже знакомо). */
data class WordToken(val text: String, val translation: String)

/** Одно утверждение [Exercise.TrueFalse]: EN-предложение, его RU-перевод и истинность. */
data class Statement(val en: String, val ru: String, val isTrue: Boolean)

/** Одна пара [Exercise.Matching]: закреплённое левое и сопоставляемое правое. */
data class MatchPair(val left: String, val right: String)

/** Одна категория [Exercise.Categorization]: заголовок-колонка и относящиеся к ней элементы. */
data class Category(val title: String, val items: List<String>)
