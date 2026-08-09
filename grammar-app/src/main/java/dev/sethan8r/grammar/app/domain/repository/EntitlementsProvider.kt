package dev.sethan8r.grammar.app.domain.repository

import dev.sethan8r.grammar.app.domain.model.subscription.Entitlements
import kotlinx.coroutines.flow.Flow

/**
 * Единый источник тира и лимитов пользователя (CLAUDE.md → чек-лист: проверка тира/лимитов — один
 * инжектируемый провайдер, в debug подменяемый). Любой код, которому нужно «можно ли AI-запрос /
 * сколько микротем осталось», берёт это ОТСЮДА, а не из разрозненных проверок.
 *
 * Фаза 1 — заглушка ([dev.sethan8r.grammar.app.data.repository.fake.FakeEntitlementsProvider]):
 * в debug отдаёт топ-тир (всё открыто), в release — Free. Реальная реализация (`GET /subscription`
 * + кэш) — Фаза 4. Лимита «слов в промте» здесь нет (отменён 12.06.2026).
 */
interface EntitlementsProvider {

    /** Текущие права/лимиты пользователя. */
    val entitlements: Flow<Entitlements>

    /** Перечитать тир/лимиты с сервера (после покупки подписки, при первом запуске за день). */
    suspend fun refresh()

    /**
     * Учесть потраченный AI-запрос (уточнение, умное задание, режим практики). Счётчик остатка на
     * AI-экранах ведёт этот провайдер, а не сами экраны — иначе у каждого была бы своя правда.
     * В Фазе 4 реализация применит остаток, пришедший в ответе сервера.
     */
    suspend fun consumeAiRequest()
}