package dev.sethan8r.grammar.app.ui.components.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.DpSize
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive

/**
 * Полоса позиции по карточкам микротемы (индикатор «на какой из [total] ты сейчас», НЕ прогресс
 * пройденности):
 *  - деления ЛЕВЕЕ [currentIndex] — оранжевые (где уже был);
 *  - деление [currentIndex] (текущее) — серое с маленькой точкой-[Accent] у левого края
 *    (как stop-indicator у Material `LinearProgressIndicator`, только слева);
 *  - деления ПРАВЕЕ — серые.
 *
 * Тап-навигация: один [pointerInput] на всю ширину, индекс — из X-координаты тапа. Зона касания =
 * жёсткая высота полосы ([Dimens.progressBarTouchHeight], [requiredHeight]).
 *
 * ⚠️ Compose в «touch»-проходе hit-теста раздувает зону ЛЮБОГО `pointerInput` до минимального
 * touch-target (~48dp, для доступности) — из-за этого тап срабатывал заметно ВНЕ тонкой полосы.
 * Обнуляем минимум локально через [LocalViewConfiguration] (`minimumTouchTargetSize = Zero`),
 * чтобы зона касания совпала с реальными границами полосы (осознанный размен на доступность —
 * деления необязательная навигация).
 */
@Composable
fun SegmentedProgressBar(
    total: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    onSegmentClick: ((Int) -> Unit)? = null,
) {
    if (total <= 0) return
    val tap = if (onSegmentClick != null) {
        Modifier.pointerInput(total) {
            detectTapGestures { offset ->
                val index = (offset.x / size.width * total).toInt().coerceIn(0, total - 1)
                onSegmentClick(index)
            }
        }
    } else {
        Modifier
    }
    val baseConfig = LocalViewConfiguration.current
    val noMinTargetConfig = remember(baseConfig) {
        object : ViewConfiguration by baseConfig {
            override val minimumTouchTargetSize: DpSize get() = DpSize.Zero
        }
    }
    CompositionLocalProvider(LocalViewConfiguration provides noMinTargetConfig) {
        Box(
            modifier = modifier
                .requiredHeight(Dimens.progressBarTouchHeight)
                .then(tap),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
            ) {
                repeat(total) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.progressBarDot),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Деление: левее текущего — оранжевое, текущее и правее — серое.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.progressBarHeight)
                                .clip(RoundedCornerShape(Dimens.progressBarHeight))
                                .background(if (index < currentIndex) Accent else Inactive),
                        )
                        // Текущая карточка — точка-маркер у левого края её деления.
                        if (index == currentIndex) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .size(Dimens.progressBarDot)
                                    .clip(CircleShape)
                                    .background(Accent),
                            )
                        }
                    }
                }
            }
        }
    }
}