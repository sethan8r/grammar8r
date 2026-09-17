package dev.sethan8r.grammar.core.repository

import dev.sethan8r.grammar.core.model.common.ApiResult
import dev.sethan8r.grammar.core.model.auth.AuthSession
import kotlinx.coroutines.flow.Flow

/**
 * Авторизация: email+пароль или Яндекс OAuth (см. subscription.md → «Авторизация», phase4_server.md).
 * Оба способа дают одинаковый uid в системе подписок. Токены живут в data-слое; домену видна
 * только текущая [AuthSession].
 *
 * Фаза 1 — заглушка ([dev.sethan8r.grammar.app.data.repository.fake.FakeAuthRepository]):
 * фиксированный dev-пользователь. Реальная реализация (HTTP + хранилище токенов) — Фаза 4.
 */
interface AuthRepository {

    /** Текущая сессия; `null` — пользователь не вошёл. */
    val session: Flow<AuthSession?>

    suspend fun registerWithEmail(email: String, password: String): ApiResult<AuthSession>

    suspend fun loginWithEmail(email: String, password: String): ApiResult<AuthSession>

    suspend fun loginWithYandex(yandexToken: String): ApiResult<AuthSession>

    suspend fun logout()
}