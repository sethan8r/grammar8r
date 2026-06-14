package dev.sethan8r.grammar.app.data.repository.fake

import dev.sethan8r.grammar.app.domain.model.progress.ProgressEvent
import dev.sethan8r.grammar.app.domain.model.progress.ProgressSnapshot
import dev.sethan8r.grammar.app.domain.repository.ProgressSyncRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заглушка синка прогресса (Фаза 1): отправка — no-op, снимок — пустой. Прогресс целиком живёт
 * локально в user.db. Реальный синк (`POST /progress/sync`, `GET /progress`) — Фаза 4.
 */
@Singleton
class FakeProgressSyncRepository @Inject constructor() : ProgressSyncRepository {

    override suspend fun push(events: List<ProgressEvent>) {
        // Заглушка: сервера нет, отправлять некуда.
    }

    override suspend fun pull(): ProgressSnapshot = ProgressSnapshot(completedCardIds = emptyList())
}