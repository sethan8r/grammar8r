package dev.sethan8r.grammar.shared

import kotlinx.serialization.Serializable

/**
 * DTO авторизации — контракт эндпоинтов `/auth/…` (см. phase4_server.md → «Авторизация»).
 *
 * Access token живёт 1 час и содержит `{ uid, tier, exp }`; refresh token — 30 дней, хранится
 * на сервере, используется только для ротации. Клиент хранит оба и обновляет access по refresh.
 */

@Serializable
data class RegisterRequest(val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class YandexAuthRequest(val yandexToken: String)

@Serializable
data class RefreshRequest(val refreshToken: String)

/** Ответ register/login/yandex: идентификатор пользователя + пара токенов. */
@Serializable
data class AuthResponse(
    val uid: String,
    val accessToken: String,
    val refreshToken: String,
)

/** Ответ `/auth/refresh`: новая пара токенов (старый refresh инвалидируется — ротация). */
@Serializable
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)