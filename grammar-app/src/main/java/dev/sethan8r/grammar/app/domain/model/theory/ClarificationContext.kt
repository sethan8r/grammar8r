package dev.sethan8r.grammar.app.domain.model.theory

/**
 * Всё, что экрану «Не совсем понял» нужно знать о карточке: что показать в шапке, какие готовые
 * вопросы предложить и какой текст теории уйдёт в промт вместе с вопросом пользователя.
 *
 * Карточка теории неизменна (content.db read-only), поэтому это снимок, а не поток.
 */
data class ClarificationContext(
    val cardId: Int,
    val cardTitle: String,
    val microtopicTitle: String,
    /** Теория карточки сплошным текстом — уходит на сервер как контекст объяснения. */
    val theoryText: String,
    /** Готовые вопросы карточки (`clarificationOptions`). */
    val options: List<String>,
)
