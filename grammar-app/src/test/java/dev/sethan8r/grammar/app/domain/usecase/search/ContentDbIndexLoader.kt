package dev.sethan8r.grammar.core.usecase.search

import dev.sethan8r.grammar.core.model.theory.IndexedMicrotopic
import dev.sethan8r.grammar.core.model.theory.IndexedTopic
import dev.sethan8r.grammar.core.model.theory.SearchIndex
import java.io.File
import java.sql.DriverManager
import java.sql.ResultSet

/**
 * Строит [SearchIndex] из настоящей content.db — той самой, что уходит в APK (путь передаёт
 * Gradle, БД пересобирается из сидов перед прогоном тестов).
 *
 * JVM-тесту недоступен Room, поэтому БД читается sqlite-драйвером. Запросы повторяют проекции
 * `TheoryDao`, а разбор тегов идёт через общий [SearchKeywords] — как в проде.
 * Прогресс живёт в user.db и в тестах не участвует: всё непройденное.
 */
object ContentDbIndexLoader {

    fun load(): SearchIndex {
        val path = System.getProperty("grammar8r.contentDb")
            ?: error("Не передан путь к content.db (systemProperty grammar8r.contentDb)")
        val db = File(path)
        check(db.isFile) { "content.db не собрана: $path" }

        DriverManager.getConnection("jdbc:sqlite:${db.absolutePath}").use { connection ->
            val sections = connection.query("SELECT id, title FROM grammar_topic_categories") {
                it.getInt("id") to it.getString("title")
            }.toMap()

            val cards = connection
                .query("SELECT id, microtopicId, title FROM grammar_cards ORDER BY `order`") {
                    it.getInt("microtopicId") to (it.getInt("id") to it.getString("title"))
                }
                .groupBy({ it.first }, { it.second })

            val microtopics = connection
                .query(
                    "SELECT id, topicId, title, `order`, searchKeywords " +
                        "FROM grammar_microtopics ORDER BY `order`"
                ) {
                    val id = it.getInt("id")
                    it.getInt("topicId") to IndexedMicrotopic(
                        id = id,
                        title = it.getString("title"),
                        keywords = SearchKeywords.parse(it.getString("searchKeywords")),
                        cardTitles = cards[id].orEmpty().map { card -> card.second },
                        cardIds = cards[id].orEmpty().map { card -> card.first },
                        order = it.getInt("order"),
                        isCompleted = false,
                    )
                }
                .groupBy({ it.first }, { it.second })

            val topics = connection.query(
                "SELECT id, title, description, categoryId, `order`, searchKeywords " +
                    "FROM grammar_topics ORDER BY `order`"
            ) { row ->
                val id = row.getInt("id")
                val categoryId = row.getInt("categoryId").takeUnless { row.wasNull() }
                IndexedTopic(
                    id = id,
                    title = row.getString("title"),
                    keywords = SearchKeywords.parse(row.getString("searchKeywords")),
                    description = row.getString("description"),
                    sectionTitle = categoryId?.let(sections::get),
                    order = row.getInt("order"),
                    completedMicrotopics = 0,
                    microtopics = microtopics[id].orEmpty(),
                )
            }

            return SearchIndex(topics)
        }
    }

    private fun <T> java.sql.Connection.query(sql: String, map: (ResultSet) -> T): List<T> =
        createStatement().use { statement ->
            statement.executeQuery(sql).use { rows ->
                buildList { while (rows.next()) add(map(rows)) }
            }
        }
}
