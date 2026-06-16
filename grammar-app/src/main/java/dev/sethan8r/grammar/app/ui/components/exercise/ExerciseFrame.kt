package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import kotlin.math.roundToInt

private const val SHAKE_DISTANCE_PX = 14f
private const val SHAKE_STEP_MS = 45
private const val SHAKE_REPEATS = 3

/**
 * Фрейм-обёртка задания (фон [CardBackground], скругление как у карточек). При неверном ответе
 * слегка трясётся — `Animatable`-смещение по X (официального API «shake» нет, по CLAUDE это
 * легитимный случай ручной анимации). Тряску запускает рост [shakeKey].
 *
 * Горизонтального паддинга у фрейма НЕТ — содержимое само добавляет отступы, а разделитель
 * ([androidx.compose.material3.HorizontalDivider]) внутри тянется от края до края фрейма.
 */
@Composable
fun ExerciseFrame(
    shakeKey: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(shakeKey) {
        if (shakeKey > 0) {
            repeat(SHAKE_REPEATS) {
                shakeOffset.animateTo(SHAKE_DISTANCE_PX, tween(SHAKE_STEP_MS))
                shakeOffset.animateTo(-SHAKE_DISTANCE_PX, tween(SHAKE_STEP_MS))
            }
            shakeOffset.animateTo(0f, tween(SHAKE_STEP_MS))
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            // Смещение через layout (не layout-параметр) — не влияет на расположение соседей.
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(IntOffset(shakeOffset.value.roundToInt(), 0))
                }
            }
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground)
            .padding(vertical = Dimens.cardPadding),
        content = content,
    )
}