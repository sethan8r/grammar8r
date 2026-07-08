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
 * (единый коннект, нулевой ретрофит).
 *
 * Порог срабатывания — по НАКОПЛЕННОМУ пути пальца в одном направлении (сбрасывается при развороте),
 * а НЕ по дельте одного кадра. Поэтому медленный скролл прячет/показывает так же, как быстрый — не
 * зависит от скорости. Показ требует меньший путь, чем скрытие ([showDistancePx] ≪ [hideDistancePx]);
 * у самого верха ([topThresholdPx]) бар всегда показан.
 */
class BottomBarScrollBehavior(
    private val showDistancePx: Float,
    private val hideDistancePx: Float,
    private val topThresholdPx: Float,
) {
    private val visibleState = mutableStateOf(true)
    val isVisible: State<Boolean> = visibleState

    // Абсолютное смещение от верха (сумма съеденного скролла) — для правила «у верха всегда показывать».
    private var offsetFromTop = 0f

    // Накопленный путь в текущем направлении: <0 палец вверх (прячем), >0 палец вниз (показываем).
    // Сбрасывается при смене направления — порог считаем по расстоянию, отсюда независимость от скорости.
    private var directionalDrag = 0f

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y // палец вверх → delta < 0; палец вниз → delta > 0
            offsetFromTop = (offsetFromTop - delta).coerceAtLeast(0f)

            // У самого верха — всегда показан, накопитель сброшен.
            if (offsetFromTop <= topThresholdPx) {
                directionalDrag = 0f
                visibleState.value = true
                return Offset.Zero
            }

            // Смена направления → копим путь заново.
            if (delta * directionalDrag < 0f) directionalDrag = 0f
            directionalDrag += delta

            when {
                directionalDrag <= -hideDistancePx -> visibleState.value = false
                directionalDrag >= showDistancePx -> visibleState.value = true
            }
            return Offset.Zero
        }
    }

    /** Принудительно показать бар (например, при смене вкладки — чтобы он не «залип» скрытым). */
    fun forceShow() {
        offsetFromTop = 0f
        directionalDrag = 0f
        visibleState.value = true
    }
}

@Composable
fun rememberBottomBarScrollBehavior(): BottomBarScrollBehavior {
    val density = LocalDensity.current
    return remember(density) {
        with(density) {
            BottomBarScrollBehavior(
                showDistancePx = Dimens.bottomBarScrollShowDistance.toPx(),
                hideDistancePx = Dimens.bottomBarScrollHideDistance.toPx(),
                topThresholdPx = Dimens.bottomBarScrollTopThreshold.toPx(),
            )
        }
    }
}