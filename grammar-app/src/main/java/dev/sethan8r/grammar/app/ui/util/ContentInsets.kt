package dev.sethan8r.grammar.app.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
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
 * капсулы + её плавающий зазор + воздух [Dimens.spaceMedium] (= зазору между темами в дереве теории).
 */
@Composable
fun floatingBarBottomInset(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
        Dimens.bottomBarFloatingHeight +
        Dimens.bottomBarFloatingBottomGap +
        Dimens.spaceMedium

/**
 * Видна ли сейчас экранная клавиатура. Единственная проверка этого в приложении (Правило №0):
 * задания с несколькими полями по ней решают, переносить ли фокус на новое поле — чтобы клавиатура
 * не мигала вниз-вверх и не всплывала сама, когда её не было.
 */
@Composable
fun isImeVisible(): Boolean = WindowInsets.ime.getBottom(LocalDensity.current) > 0

/**
 * Верхний отступ скроллящегося контента экрана, который уходит edge-to-edge под строку состояния
 * (корневые вкладки, экраны с `PinnedHeader`) = высота строки состояния. Строку перекрывает скрим, поэтому первый
 * элемент держим под ней сами этим отступом — при скролле контент проезжает под строкой состояния.
 */
@Composable
fun statusBarTopInset(): Dp =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()