package dev.sethan8r.grammar.app.data.repository.fake

import dev.sethan8r.grammar.app.domain.model.common.ApiResult
import dev.sethan8r.grammar.app.domain.model.auth.AuthSession
import dev.sethan8r.grammar.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заглушка авторизации (Фаза 1): фиксированный dev-пользователь, всегда «вошедший».
 * Любой вход возвращает успех; [logout] обнуляет сессию. Реальная реализация — Фаза 4.
 */
@Singleton
class FakeAuthRepository @Inject constructor() : AuthRepository {

    private val devSession = AuthSession(uid = "dev-user", email = "dev@grammar8r.local")
    private val _session = MutableStateFlow<AuthSession?>(devSession)
    override val session = _session.asStateFlow()

    override suspend fun registerWithEmail(email: String, password: String): ApiResult<AuthSession> =
        signInFake()

    override suspend fun loginWithEmail(email: String, password: String): ApiResult<AuthSession> =
        signInFake()

    override suspend fun loginWithYandex(yandexToken: String): ApiResult<AuthSession> =
        signInFake()

    override suspend fun logout() {
        _session.value = null
    }

    private fun signInFake(): ApiResult<AuthSession> {
        _session.value = devSession
        return ApiResult.Success(devSession)
    }
}