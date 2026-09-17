package dev.sethan8r.grammar.core.model.theory

/**
 * Плоский снимок курса для поиска: всё, по чему ищем, и всё, что показываем в выдаче.
 * Собирается репозиторием отдельным потоком (не частью [TheoryData]) — карточки нужны только
 * поиску, при обычном показе вкладки их грузить незачем.
 *
 * Механика поиска — search_feature_brief.md §8.3.
 */
data class SearchIndex(val topics: List<IndexedTopic>)

/** Тема со своими полями индекса и прогрессом для шапки группы. */
data class IndexedTopic(
    val id: Int,
    val title: String,
    val keywords: List<String>,
    val description: String?,
    /** Название раздела — подпись над темой в выдаче. Разделом сам по себе поиск не ищет. */
    val sectionTitle: String?,
    val order: Int,
    val completedMicrotopics: Int,
    val microtopics: List<IndexedMicrotopic>,
)

/**
 * Микротема со своими полями индекса. [cardTitles] — скрытый сигнал релевантности: карточки
 * дают очки своей микротеме, но в выдаче не показываются (§8.2.2). [cardIds] в ранжировании не
 * участвуют — по ним ищется микротема, когда запрос состоит из одних цифр (номер с бейджа читалки).
 */
data class IndexedMicrotopic(
    val id: Int,
    val title: String,
    val keywords: List<String>,
    val cardTitles: List<String>,
    val cardIds: List<Int>,
    val order: Int,
    val isCompleted: Boolean,
)

/**
 * Группа выдачи: тема-шапка и совпавшие под ней микротемы. Тема, совпавшая сама по себе, даёт
 * группу с пустым [microtopics] — это валидный результат, а не пустышка.
 *
 * Несёт готовые [TopicSummary]/[MicrotopicSummary] — те же модели, что рисуют дерево теории,
 * поэтому выдача переиспользует компоненты вкладки без параметров-исключений.
 */
data class SearchGroup(
    val topic: TopicSummary,
    val sectionTitle: String?,
    val microtopics: List<MicrotopicSummary>,
    val score: Int,
)
