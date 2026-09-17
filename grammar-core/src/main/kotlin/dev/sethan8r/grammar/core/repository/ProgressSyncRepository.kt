package dev.sethan8r.grammar.core.repository

import dev.sethan8r.grammar.core.model.progress.ProgressEvent
import dev.sethan8r.grammar.core.model.progress.ProgressSnapshot

/**
 * Синхронизация прогресса с сервером (см. phase4_server.md → «Синхронизация прогресса»):
 * [push] — отправка офлайн-буфера событий пачкой при появлении сети; [pull] — снимок прогресса
 * при переустановке/смене телефона.
 *
 * Фаза 1 — заглушка ([dev.sethan8r.grammar.app.data.repository.fake.FakeProgressSyncRepository]):
 * no-op / пустой снимок. Реальная реализация — Фаза 4.
 */
interface ProgressSyncRepository {

    suspend fun push(events: List<ProgressEvent>)

    suspend fun pull(): ProgressSnapshot
}