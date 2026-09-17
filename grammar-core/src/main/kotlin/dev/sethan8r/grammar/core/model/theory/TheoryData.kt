package dev.sethan8r.grammar.core.model.theory

/**
 * Сырое объединение источников дерева теории (content.db + прогресс из user.db), которое
 * репозиторий отдаёт одним снимком. Превращение в готовый [TheoryListItem]-список — задача
 * чистого [dev.sethan8r.grammar.core.usecase.GetTheoryListUseCase] (тестируется без Android).
 */
data class TheoryData(
    val categories: List<TheoryCategory>,
    val topics: List<TheoryTopic>,
    /** Микротемы, сгруппированные по теме и упорядоченные внутри неё. */
    val microtopicsByTopic: Map<Int, List<TheoryMicrotopicRef>>,
    val completedMicrotopicIds: Set<Int>,
)

data class TheoryCategory(val id: Int, val title: String, val description: String?, val order: Int)

data class TheoryTopic(
    val id: Int,
    val title: String,
    val description: String?,
    val isPretopic: Boolean,
    val categoryId: Int?,
    val order: Int,
)

data class TheoryMicrotopicRef(val id: Int, val title: String, val order: Int)

/** Данные экрана темы: её заголовок + список микротем с прогрессом. */
data class TopicMicrotopics(val topicTitle: String, val microtopics: List<MicrotopicSummary>)

/** Данные экрана микротемы: её заголовок + карточки + id пройденных карточек (для слота «умное задание»). */
data class MicrotopicCards(
    val microtopicTitle: String,
    val cards: List<TheoryCard>,
    val completedCardIds: Set<Int>,
)