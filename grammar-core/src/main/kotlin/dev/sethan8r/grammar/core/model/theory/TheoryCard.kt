package dev.sethan8r.grammar.core.model.theory

/**
 * Карточка теории в доменном виде — то, что листает пользователь внутри микротемы.
 * Получается из `GrammarCard` (Entity) маппером: сырой JSON полей `theory`/`examples`/
 * `clarificationOptions` уже разобран в типизированные [blocks]/[examples]/[clarificationOptions].
 */
data class TheoryCard(
    val id: Int,
    val microtopicId: Int,
    val title: String,
    val order: Int,
    val blocks: List<TheoryBlock>,
    /** Краткое правило карточки блоками (окно «?» в упражнениях). */
    val summary: List<TheoryBlock>,
    val examples: List<Example>,
    /** Готовые вопросы для будущей фичи «Не совсем понял» (Фаза 3). */
    val clarificationOptions: List<String>,
    /**
     * Есть ли у карточки задания (хардкод- ИЛИ AI-упражнения). Определяет основную кнопку: есть →
     * «Перейти к заданиям» (сессия); нет → «Завершить карточку» (отметка + переход к следующей/назад).
     */
    val hasExercises: Boolean = false,
    /** Есть ли у карточки умное (AI) задание — по нему показывается кнопка «Перейти к умному заданию». */
    val hasAiExercise: Boolean = false,
)

/** Пара «русский → английский» из секции Examples карточки. */
data class Example(val ru: String, val en: String)