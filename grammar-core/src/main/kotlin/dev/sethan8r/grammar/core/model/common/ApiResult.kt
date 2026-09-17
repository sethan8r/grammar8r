package dev.sethan8r.grammar.core.model.common

/**
 * Результат запроса к серверу: успех с данными либо типизированная ошибка.
 * Возвращается всеми репозиториями, которые ходят на наш бэкенд (auth, AI, ...).
 *
 * Ошибки — enum [RequestError], а не строки (правило «никакого stringly-typed»). Набор шире
 * серверного: кроме кодов сервера он включает транспортные ([NETWORK]) и неизвестные ([UNKNOWN])
 * ошибки, которых сервер не присылает. Поэтому это доменный enum, а не shared-контракт. Сам
 * контрактный enum кодов и маппинг «код сервера → [RequestError]» появятся вместе с реальным
 * remote-слоем (Фаза 4), где их будет кому десериализовать — сейчас это был бы мёртвый код.
 */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Failure(val error: RequestError) : ApiResult<Nothing>
}

/** Доменная классификация ошибки запроса (server-коды + транспорт). */
enum class RequestError {
    LIMIT_EXCEEDED,   // дневной лимит AI-запросов исчерпан
    AI_ERROR,         // сервер не смог сгенерировать/оценить
    PROMPT_NOT_FOUND, // нет промта под exerciseId
    ACCOUNT_BANNED,   // аккаунт заблокирован
    UNAUTHORIZED,     // токен невалиден/истёк
    NETWORK,          // нет связи / таймаут (клиентская)
    UNKNOWN,          // всё прочее
}