package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Контейнер с равномерным ореолом-тенью вокруг формы [shape]: позади содержимого лежит чёрный слой
 * той же формы, размытый наружу. Получается кольцо «чёрный у края → прозрачный» одинаковой толщины
 * со всех сторон, поэтому парящий элемент отделяется от того, что лежит под ним.
 *
 * Содержимое рисуется поверх ореола и перекрывает его центр, так что размер задаёт само содержимое.
 * Используют капсула нижней навигации и плавающая кнопка закрытия в `InfoDialog`.
 */
@Composable
fun HaloBox(
    shape: Shape,
    modifier: Modifier = Modifier,
    blurRadius: Dp = Dimens.haloBlur,
    alpha: Float = Alphas.halo,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(blurRadius, BlurredEdgeTreatment.Unbounded)
                .background(Color.Black.copy(alpha = alpha), shape),
        )
        content()
    }
}
