package dev.sethan8r.grammar.app.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Тактильный отклик на ответ в упражнениях:
 * - неверный — короткая серия толчков «в такт» тряске карточки (`ExerciseFrame`);
 * - верный — лёгкий одиночный «тык» в такт пульсу карточки.
 *
 * Compose-овский `LocalHapticFeedback` умеет лишь одиночный «тик» предопределённого типа и не задаёт
 * паттерн нужной длительности, поэтому берём системный [Vibrator] с [VibrationEffect] — CLAUDE.md
 * разрешает ручной путь, когда официальный API не покрывает задачу.
 */

/** Паттерн «в такт тряске»: 6 толчков ~35 мс с паузами 10 мс (≈270 мс при тряске ≈315 мс). */
private val WRONG_ANSWER_PATTERN = longArrayOf(0, 35, 10, 35, 10, 35, 10, 35, 10, 35, 10, 35)

/** Длительность лёгкого «тыка» на верном ответе. */
private const val CORRECT_TICK_MS = 20L

/** «Не повторять» для [VibrationEffect.createWaveform]. */
private const val NO_REPEAT = -1

/** Триггер вибрации неверного ответа (серия толчков). На устройствах без мотора — no-op. */
@Composable
fun rememberWrongAnswerVibration(): () -> Unit {
    val vibrator = rememberVibrator()
    return remember(vibrator) {
        { vibrator.play(VibrationEffect.createWaveform(WRONG_ANSWER_PATTERN, NO_REPEAT)) }
    }
}

/** Триггер вибрации верного ответа (лёгкий «тык»). На устройствах без мотора — no-op. */
@Composable
fun rememberCorrectAnswerVibration(): () -> Unit {
    val vibrator = rememberVibrator()
    return remember(vibrator) {
        { vibrator.play(VibrationEffect.createOneShot(CORRECT_TICK_MS, VibrationEffect.DEFAULT_AMPLITUDE)) }
    }
}

/** Системный [Vibrator], запомненный на время жизни composable (привязка к [Context]). */
@Composable
private fun rememberVibrator(): Vibrator? {
    val context = LocalContext.current
    return remember(context) { context.vibratorOrNull() }
}

/** Проигрывает эффект, если вибромотор есть; иначе ничего не делает. */
private fun Vibrator?.play(effect: VibrationEffect) {
    if (this != null && hasVibrator()) vibrate(effect)
}

/** Системный [Vibrator]: на API 31+ — через [VibratorManager], раньше — напрямую. */
private fun Context.vibratorOrNull(): Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        getSystemService(Vibrator::class.java)
    }