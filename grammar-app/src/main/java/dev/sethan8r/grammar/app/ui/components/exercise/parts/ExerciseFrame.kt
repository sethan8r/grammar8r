package dev.sethan8r.grammar.app.ui.components.exercise.parts

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.util.rememberCorrectAnswerVibration
import dev.sethan8r.grammar.app.ui.util.rememberWrongAnswerVibration
import kotlin.math.roundToInt

private const val SHAKE_DISTANCE_PX = 14f
private const val SHAKE_STEP_MS = 45
private const val SHAKE_REPEATS = 3

/** Пульс на верном ответе: масштаб 1f → [PULSE_SCALE] → 1f (как карточка слова в Words8r). */
private const val PULSE_SCALE = 1.05f
private const val PULSE_STEP_MS = 300

/**
 * Фрейм-обёртка задания (фон [CardBackground], скругление как у карточек). Реагирует на ответ:
 * - неверный ([shakeKey] вырос) — слегка трясётся (`Animatable`-смещение по X) + вибрация «в такт»;
 * - верный ([pulseKey] вырос) — на миг увеличивается и возвращается к размеру.
 *
 * Официального API «shake»/«pulse» нет — по CLAUDE это легитимный случай ручной анимации. Тряска и
 * пульс взаимоисключающи по событию (на одно нажатие растёт ровно один из ключей).
 *
 * Горизонтального паддинга у фрейма НЕТ — содержимое само добавляет отступы, а разделитель
 * ([androidx.compose.material3.HorizontalDivider]) внутри тянется от края до края фрейма.
 */
@Composable
fun ExerciseFrame(
    shakeKey: Int,
    pulseKey: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shakeOffset = remember { Animatable(0f) }
    val cardScale = remember { Animatable(1f) }
    val triggerWrongVibration = rememberWrongAnswerVibration()
    val triggerCorrectVibration = rememberCorrectAnswerVibration()

    LaunchedEffect(shakeKey) {
        if (shakeKey > 0) {
            triggerWrongVibration()
            repeat(SHAKE_REPEATS) {
                shakeOffset.animateTo(SHAKE_DISTANCE_PX, tween(SHAKE_STEP_MS))
                shakeOffset.animateTo(-SHAKE_DISTANCE_PX, tween(SHAKE_STEP_MS))
            }
            shakeOffset.animateTo(0f, tween(SHAKE_STEP_MS))
        }
    }
    LaunchedEffect(pulseKey) {
        if (pulseKey > 0) {
            triggerCorrectVibration()
            cardScale.animateTo(PULSE_SCALE, tween(PULSE_STEP_MS))
            cardScale.animateTo(1f, tween(PULSE_STEP_MS))
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            // Пульс через graphicsLayer — масштаб от центра, не влияет на layout соседей.
            .graphicsLayer {
                scaleX = cardScale.value
                scaleY = cardScale.value
            }
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