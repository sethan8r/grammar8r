package dev.sethan8r.grammar.app.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Нижний отступ скроллящегося контента на полноэкранных экранах (без боттом-бара). Контент уходит
 * edge-to-edge под прозрачную системную полосу навигации, а этот отступ держит последний элемент
 * над жестами/кнопками: высота навбар-инсета + единый визуальный зазор [Dimens.bottomBarGap]
 * (чтобы «воздух» не разъезжался по экранам). Кладётся в `contentPadding` (LazyColumn) или в
 * `padding(bottom = …)` (Column + verticalScroll).
 * "extra" = сколько добавляем дял компенсации
 */
@Composable
fun scrollBottomInset(extra: Dp = Dimens.bottomBarGap24): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + extra