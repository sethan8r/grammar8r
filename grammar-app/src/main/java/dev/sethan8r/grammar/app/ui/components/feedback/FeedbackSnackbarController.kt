package dev.sethan8r.grammar.app.ui.components.feedback

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import dev.sethan8r.grammar.app.ui.theme.Durations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Контроллер фидбэк-снекбара с семантикой «заменять, а не копить»: новый [show] мгновенно гасит
 * текущий снекбар и показывает свой, НЕ дожидаясь авто-исчезновения предыдущего.
 *
 * Зачем: штатный `showSnackbar` — suspend до закрытия снекбара, поэтому коллектор событий залипает
 * внутри него, а следующие события копятся в очереди и показываются по одному (см. баг с двойным
 * «Неправильно» и описаниями тем). Здесь каждый показ — отдельный отменяемый job: новый показ
 * отменяет предыдущий и снимает плашку сразу.
 *
 * Единый механизм для всех экранов (Правило №0): ошибки упражнений и описания тем из «i».
 */
class FeedbackSnackbarController(
    val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    private var job: Job? = null

    // Палец на плашке: пока true — обратный отсчёт заморожен (снекбар не исчезает, даже если время вышло).
    private var held = false

    /** Зажатие/отпускание плашки. При зажатии таймер стоит; после отпускания досчитывается (см. [show]). */
    fun setHeld(down: Boolean) {
        held = down
    }

    /**
     * Показать [message]; авто-исчезновение через [durationMs]. Предыдущий снекбар снимается сразу.
     *
     * Таймер паузится, пока палец на плашке ([held]): остаток замирает. Если время вышло, пока держали —
     * после отпускания добавляется [Durations.snackbarHoldGraceMs] и снекбар уходит.
     */
    fun show(message: String, durationMs: Long) {
        job?.cancel()
        hostState.currentSnackbarData?.dismiss()
        held = false
        job = scope.launch {
            // Indefinite + ручной таймер: точная длительность в мс (штатный enum даёт лишь Short/Long).
            // showSnackbar — дочерняя корутина job: при следующем show() job отменяется → плашка уходит.
            launch { hostState.showSnackbar(message, duration = SnackbarDuration.Indefinite) }
            var remaining = durationMs
            while (remaining > 0) {
                delay(TICK_MS)
                if (!held) remaining -= TICK_MS // пока держат — остаток не тратится
            }
            // Время вышло: если ещё держат — ждём отпускания и даём короткую отсрочку.
            if (held) {
                while (held) delay(TICK_MS)
                delay(Durations.snackbarHoldGraceMs)
            }
            hostState.currentSnackbarData?.dismiss()
        }
    }

    private companion object {
        /** Шаг ручного таймера, мс (достаточно частый для плавной паузы/резюма по пальцу). */
        const val TICK_MS = 50L
    }
}

/** Контроллер общего снекбара корневых вкладок (висит над капсулой навигации в MainScreen). */
val LocalTabSnackbarController = staticCompositionLocalOf<FeedbackSnackbarController?> { null }

@Composable
fun rememberFeedbackSnackbarController(): FeedbackSnackbarController {
    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    return remember(hostState, scope) { FeedbackSnackbarController(hostState, scope) }
}