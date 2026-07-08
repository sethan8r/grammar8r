package dev.sethan8r.grammar.app.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Нижний отступ скроллящегося контента на полноэкранных экранах (без боттом-бара). Контент уходит
 * edge-to-edge под прозрачную системную полосу навигации, а этот отступ держит последний элемент
 * над жестами/кнопками: высота навбар-инсета + единый визуальный зазор [Dimens.bottomBarGap]
 * (чтобы «воздух» не разъезжался по экранам). Кладётся в `contentPadding` (LazyColumn) или в
 * `padding(bottom = …)` (Column + verticalScroll).
 * "extra" = сколько добавляем для компенсации
 */
@Composable
fun scrollBottomInset(extra: Dp = Dimens.bottomBarGap24): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + extra

/**
 * Нижний отступ скроллящегося контента КОРНЕВОЙ вкладки. Плавающая капсула навигации
 * ([dev.sethan8r.grammar.app.ui.components.scaffold.Grammar8rBottomBar]) парит поверх контента и не
 * резервирует высоту, поэтому последний элемент списка держим над ней сами: навбар-инсет + высота
 * капсулы + её плавающий зазор + тонкий воздух.
 */
@Composable
fun floatingBarBottomInset(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
        Dimens.bottomBarFloatingHeight +
        Dimens.bottomBarFloatingBottomGap +
        Dimens.bottomBarGap8

/**
 * Верхний отступ скроллящегося контента КОРНЕВОЙ вкладки = высота строки состояния. На вкладках
 * контент уходит edge-to-edge под строку состояния (её перекрывает градиент-скрим), поэтому первый
 * элемент держим под ней сами этим отступом — при скролле контент проезжает под строкой состояния.
 */
@Composable
fun statusBarTopInset(): Dp =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()