package dev.sethan8r.grammar.app.ui.components.scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Единый источник правды по видимости плавающей нижней навигации ([Grammar8rBottomBar]) при скролле.
 *
 * Держит один [nestedScrollConnection], который вешается на `NavHost` — благодаря всплытию
 * nested-scroll он ловит скролл ЛЮБОЙ вкладки, поэтому сами экраны вкладок ничего не прокидывают
 * (единый коннект, нулевой ретрофит). Правила показа/скрытия — из tasks/README.md §«Появление/исчезание»:
 * скролл вниз прячет бар только вдали от верха; скролл вверх (или у самого верха) — сразу показывает.
 */
class BottomBarScrollBehavior(
    private val deadZonePx: Float,
    private val topThresholdPx: Float,
) {
    private val visibleState = mutableStateOf(true)
    val isVisible: State<Boolean> = visibleState

    // Накопленное смещение от верха контента (сумма съеденного скролла). nestedScroll даёт дельты,
    // а правило «у верха всегда показывать» требует абсолютной позиции — копим её сами.
    private var offsetFromTop = 0f

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            // Палец вверх (скролл контента вниз) → delta < 0; палец вниз → delta > 0.
            offsetFromTop = (offsetFromTop - delta).coerceAtLeast(0f)
            when {
                delta < -deadZonePx && offsetFromTop > topThresholdPx -> visibleState.value = false
                delta > deadZonePx || offsetFromTop <= topThresholdPx -> visibleState.value = true
            }
            return Offset.Zero
        }
    }

    /** Принудительно показать бар (например, при смене вкладки — чтобы он не «залип» скрытым). */
    fun forceShow() {
        offsetFromTop = 0f
        visibleState.value = true
    }
}

@Composable
fun rememberBottomBarScrollBehavior(): BottomBarScrollBehavior {
    val density = LocalDensity.current
    return remember(density) {
        with(density) {
            BottomBarScrollBehavior(
                deadZonePx = Dimens.bottomBarScrollDeadZone.toPx(),
                topThresholdPx = Dimens.bottomBarScrollTopThreshold.toPx(),
            )
        }
    }
}