package dev.sethan8r.grammar.app.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

    /** Показать [message]; авто-исчезновение через [durationMs]. Предыдущий снекбар снимается сразу. */
    fun show(message: String, durationMs: Long) {
        job?.cancel()
        hostState.currentSnackbarData?.dismiss()
        job = scope.launch {
            // Indefinite + ручной таймер: точная длительность в мс (штатный enum даёт лишь Short/Long).
            // showSnackbar — дочерняя корутина job: при следующем show() job отменяется → плашка уходит.
            launch { hostState.showSnackbar(message, duration = SnackbarDuration.Indefinite) }
            delay(durationMs)
            hostState.currentSnackbarData?.dismiss()
        }
    }
}

@Composable
fun rememberFeedbackSnackbarController(): FeedbackSnackbarController {
    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    return remember(hostState, scope) { FeedbackSnackbarController(hostState, scope) }
}