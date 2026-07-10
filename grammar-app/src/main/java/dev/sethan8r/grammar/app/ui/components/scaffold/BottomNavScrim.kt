package dev.sethan8r.grammar.app.ui.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background

/**
 * Градиент-скрим над системной полосой навигации — зеркальный близнец [TopStatusScrim], для ВСЕХ
 * экранов (вкладки и полноэкранные роуты; рисуется один раз оверлеем в MainScreen). Контент уходит
 * edge-to-edge под прозрачную системную полосу; скрим гасит проезжающий контент: прозрачность сверху
 * плавно переходит в [Background] на [Alphas.statusScrim], нижняя часть полосы — константа. Высота =
 * инсет полосы навигации, ширина — на весь экран.
 */
@Composable
fun BottomNavScrim(modifier: Modifier = Modifier) {
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val scrim = Background.copy(alpha = Alphas.statusScrim)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(navBarHeight)
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,   // верх полосы навигации — прозрачно
                    0.8f to scrim,             // плавно в скрим
                    1f to scrim,               // нижний край — константа
                ),
            ),
    )
}
