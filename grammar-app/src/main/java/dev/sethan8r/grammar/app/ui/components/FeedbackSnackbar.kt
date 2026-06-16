package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary

/**
 * Фидбэк-снекбар на ШТАТНОМ [SnackbarHost] (показ/очередь/авто-исчезновение — из коробки),
 * смахиваемый через [SwipeToDismissBox] (как в Words8r, но без самопального хоста и таймера).
 * Текст всегда обычный ([TextPrimary]). Используется для ошибок хардкод-упражнений
 * («Неправильно…»); на верный ответ ничего не показываем.
 */
@Composable
fun FeedbackSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
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
            // Свайп-зона на всю ширину, но сама плашка — по ширине текста и по ЦЕНТРУ.
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.cornerCard))
                        .background(CardBackground)
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