package dev.sethan8r.grammar.app.ui.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background

/**
 * Градиент-скрим над строкой состояния для корневых вкладок. Контент вкладки уходит edge-to-edge под
 * строку состояния; этот скрим лёг поверх него (но под системными часами/иконками, их рисует система)
 * и гасит проезжающий контент: [Background] на [Alphas.statusScrim] держится верхнюю половину строки
 * состояния, затем плавно уходит в прозрачность к её низу. Высота = инсет строки состояния, ширина —
 * на весь экран.
 */
@Composable
fun TopStatusScrim(modifier: Modifier = Modifier) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val scrim = Background.copy(alpha = Alphas.statusScrim)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(statusBarHeight)
            .background(
                Brush.verticalGradient(
                    0f to scrim,          // верх строки состояния
                    0.2f to scrim,                     // до середины — константа
                    1f to Color.Transparent,           // ниже — плавно в ноль к низу строки состояния
                ),
            ),
    )
}