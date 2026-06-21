package dev.sethan8r.grammar.app.ui.theme

import androidx.compose.ui.graphics.Color

val Background     = Color(0xFF121318)           // было 0xFF15171C
val CardBackground = Color(0xFF191B21)       // было 0xFF1D2027
val Elevated       = Color(0xFF272C36)             // было 0xFF2E3440  // поверхность НАД карточкой (снекбары/уведомления) — светлее фрейма, чтобы не сливаться
val TextPrimary    = Color(0xFFFFFFFF)
val TextSecondary  = Color(0xFFB0B0B0)
val Accent         = Color(0xFF6B84D9)               // было 0xFF7E9BFF  // основной акцент: кнопки, прогресс, активные элементы
val Inactive       = Color(0xFF404040)
val CorrectGreen   = Color(0xFF4DAB75)         // было 0xFF5BC98A
val IncorrectRed   = Color(0xFFCF5E61)         // было 0xFFF46E72  // ошибка, ✗, плашка-ловушка

/**
 * Оранжевый «маркер внимания» — больше НЕ акцент приложения (акцент теперь [Accent], синий).
 * Используется точечно и только для врезок-callout WARNING/FORMULA в теории (CalloutVariant).
 * Не применять для кнопок/прогресса/активных элементов — там [Accent].
 */
val Highlight      = Color(0xFFFF9B27)