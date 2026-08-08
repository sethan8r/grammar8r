package dev.sethan8r.grammar.app.domain.usecase.search

/**
 * Разбор поля `searchKeywords` (теги через запятую одной строкой) в список тегов.
 * Единственное место разбора: им пользуются и маппер индекса, и тесты на content.db.
 */
object SearchKeywords {

    fun parse(raw: String?): List<String> =
        raw?.split(',')?.map(String::trim)?.filter(String::isNotEmpty).orEmpty()
}
