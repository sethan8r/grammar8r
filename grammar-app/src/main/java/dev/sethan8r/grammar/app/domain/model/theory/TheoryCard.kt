package dev.sethan8r.grammar.app.domain.model.theory

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
    /** Краткое резюме правила (кнопка «?» в упражнениях — Фаза F). */
    val summary: String,
    val examples: List<Example>,
    /** Готовые вопросы для будущей фичи «Не совсем понял» (Фаза 3). */
    val clarificationOptions: List<String>,
)

/** Пара «русский → английский» из секции Examples карточки. */
data class Example(val ru: String, val en: String)