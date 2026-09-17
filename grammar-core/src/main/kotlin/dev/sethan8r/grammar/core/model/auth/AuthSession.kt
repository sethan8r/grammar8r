package dev.sethan8r.grammar.core.model.auth

/**
 * Доменная сессия пользователя — то, что хранит
 * [dev.sethan8r.grammar.core.repository.AuthRepository]. Токены (access/refresh) живут в
 * data-слое (защищённое хранилище), наружу домену отдаётся только идентичность.
 *
 * `email == null` — вход через Яндекс (email из аккаунта Яндекса не обязателен).
 */
data class AuthSession(
    val uid: String,
    val email: String?,
)