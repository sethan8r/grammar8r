package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Короткое сообщение-подсказка по центру отведённой области (пустые/информационные состояния:
 * «Пусто», «Ничего не найдено» и т.п.). Размер/позицию задаёт вызывающий через [modifier].
 */
@Composable
fun CenteredHint(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextSecondary,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = text, color = color)
    }
}