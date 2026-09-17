package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Индикатор загрузки — крутящийся кружок по центру отведённой области. Размер/позицию задаёт
 * вызывающий через [modifier] (например `Modifier.fillMaxSize()` — на весь экран). Цвет по
 * умолчанию белый ([TextSecondary]).
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = TextSecondary,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = color)
    }
}