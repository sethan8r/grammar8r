package dev.sethan8r.grammar.app.domain.model

/**
 * Элемент верхнего уровня вкладки «Учить» (список теории). Раздел и отдельная тема делят одно
 * пространство порядка (см. theory_content_guide.md), поэтому это один разнотипный список.
 */
sealed interface TheoryListItem {

    /** Тема без раздела — показывается карточкой напрямую. */
    data class TopicItem(val topic: TopicSummary) : TheoryListItem

    /** Раздел («Устройство языка») — заголовок с вложенными темами. */
    data class SectionItem(
        val id: Int,
        val title: String,
        val description: String?,
        val topics: List<TopicSummary>,
    ) : TheoryListItem
}

/** Краткая сводка по теме для списка: заголовок + прогресс по микротемам. */
data class TopicSummary(
    val id: Int,
    val title: String,
    val description: String?,
    val isPretopic: Boolean,
    val completedMicrotopics: Int,
    val totalMicrotopics: Int,
)

/** Сводка по микротеме внутри темы. */
data class MicrotopicSummary(
    val id: Int,
    val title: String,
    val state: MicrotopicState,
)

/**
 * Состояние микротемы — enum, не Boolean (закладка foundation_plan §7: позже добавятся
 * «заблокирована лимитом», «есть неоткрытые слова»). На Шаге E (только чтение) реальны два:
 * микротема либо пройдена, либо доступна.
 */
enum class MicrotopicState { AVAILABLE, COMPLETED }