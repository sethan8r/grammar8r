package dev.sethan8r.grammar.app.ui.util

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Затемняющая подложка снизу: вертикальный градиент от прозрачного (верх) до [alpha] от [color].
 * Насыщение достигается не в самом низу, а к точке [saturateAt] (доля высоты, 0..1) — выше неё
 * затемнение разгоняется плавно, ниже — ровная [alpha]. Так у затемнения нет резкой верхней кромки.
 */
fun Modifier.bottomScrim(
    color: Color = Background,
    alpha: Float = Alphas.footerScrim,
    saturateAt: Float = 1f / 3f,
): Modifier = background(
    Brush.verticalGradient(
        0f to color.copy(alpha = 0f),
        saturateAt to color.copy(alpha = alpha),
        1f to color.copy(alpha = alpha),
    ),
)

/**
 * Затемняющая подложка закреплённой шапки, под которую проезжает контент: ровная [alpha] от [color]
 * сверху, а нижние [fade] высоты плавно уходят в прозрачность — у затемнения нет резкой нижней кромки.
 */
fun Modifier.topScrim(
    fade: Dp = Dimens.headerScrimFade,
    color: Color = Background,
    alpha: Float = Alphas.headerScrim,
): Modifier = drawBehind {
    val solidUntil = (1f - fade.toPx() / size.height).coerceIn(0f, 1f)
    drawRect(
        Brush.verticalGradient(
            0f to color.copy(alpha = alpha),
            solidUntil to color.copy(alpha = alpha),
            1f to color.copy(alpha = 0f),
        ),
    )
}