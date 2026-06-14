package dev.sethan8r.grammar.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Тир подписки — enum контракта клиент↔сервер. Сервер может прислать ЛЮБОЕ из пяти значений
 * (в `SubscriptionDto.tier`), поэтому полный набор должен существовать в коде на обеих сторонах.
 *
 * ⚠️ Видимость в UI: пользователю на экранах подписки доступны только тиры с [userVisible] = true
 * (FREE / TIER1 / TIER2). `ADMIN` и `TESTER` — внутренние, выдаются вручную через серверную панель;
 * обычные пользователи о них не знают и в списках их не видят. Экраны итерируют по [userVisibleTiers],
 * а НЕ по всем `entries`.
 *
 * Источник правды по семантике/лимитам — subscription.md.
 */
@Serializable
enum class SubscriptionTier(val userVisible: Boolean) {
    @SerialName("free")   FREE(userVisible = true),
    @SerialName("tier1")  TIER1(userVisible = true),
    @SerialName("tier2")  TIER2(userVisible = true),
    @SerialName("tester") TESTER(userVisible = false),
    @SerialName("admin")  ADMIN(userVisible = false);

    companion object {
        /** Тиры, которые разрешено показывать пользователю (экран подписки, «Моя подписка»). */
        val userVisibleTiers: List<SubscriptionTier> get() = entries.filter { it.userVisible }
    }
}