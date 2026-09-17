package dev.sethan8r.grammar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = Accent,
    onPrimary        = Background,
    secondary        = TextSecondary,
    background       = Background,
    onBackground     = TextPrimary,
    surface          = CardBackground,
    onSurface        = TextPrimary,
    surfaceVariant   = CardBackground,
    onSurfaceVariant = TextSecondary,
)

@Composable
fun Grammar8rTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}