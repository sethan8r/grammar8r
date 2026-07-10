package dev.sethan8r.grammar.app.ui.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.TextPrimary

/**
 * Фидбэк-снекбар на ШТАТНОМ [SnackbarHost] (показ/очередь/авто-исчезновение — из коробки),
 * смахиваемый через [SwipeToDismissBox] (как в Words8r, но без самопального хоста и таймера).
 * Текст всегда обычный ([TextPrimary]). Используется для ошибок хардкод-упражнений
 * («Неправильно…»); на верный ответ ничего не показываем.
 *
 * [onHoldChanged] дёргается при зажатии/отпускании плашки (для паузы авто-таймера в контроллере).
 * Жест наблюдается без потребления событий — свайп-дисмисс продолжает работать.
 */
@Composable
fun FeedbackSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onHoldChanged: (Boolean) -> Unit = {},
) {
    SnackbarHost(hostState, modifier) { data ->
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value != SwipeToDismissBoxValue.Settled) {
                    data.dismiss()
                    true
                } else {
                    false
                }
            },
        )
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {},
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
        ) {
            // Свайп-зона на всю ширину, но сама плашка — по ширине текста и по ЦЕНТРУ. Кеп ширины
            // не задаём: плашку ограничивает сам хост (его ширина = ширине фрейма темы, screenPadding),
            // поэтому длинный текст переносится по ширине фрейма, а короткий жмётся к своей ширине.
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.cornerCard))
                        .background(Elevated)
                        // Наблюдаем зажатие плашки, НЕ потребляя события (swipe-dismiss не ломаем).
                        .pointerInput(Unit) {
                            try {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitFirstDown(requireUnconsumed = false)
                                        onHoldChanged(true)
                                        do {
                                            val event = awaitPointerEvent()
                                        } while (event.changes.any { it.pressed })
                                        onHoldChanged(false)
                                    }
                                }
                            } finally {
                                // Плашка ушла из композиции с зажатым пальцем (свайп-дисмисс): корутина
                                // отменяется без события отпускания — снимаем «зажато», иначе таймер
                                // контроллера навсегда останется на паузе.
                                onHoldChanged(false)
                            }
                        }
                        .padding(horizontal = Dimens.spaceXLarge, vertical = Dimens.spaceMedium),
                    contentAlignment = Alignment.Center,
                ) {
                    MarkdownText(
                        text = data.visuals.message,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}