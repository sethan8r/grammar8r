package dev.sethan8r.grammar.app.data.local.content.dao.projection

/**
 * Проекция карточки для поискового индекса: только заголовок, без тела теории.
 * Тело в индекс не входит (FTS отвергнут — search_feature_brief.md §8.3.1), а тянуть его
 * ради названия значит грузить весь курс в память.
 */
data class CardTitle(
    val id: Int,
    val microtopicId: Int,
    val title: String,
)
