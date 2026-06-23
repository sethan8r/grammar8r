package dev.sethan8r.grammar.app.ui.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.sethan8r.grammar.app.ui.navigation.TopLevelDestination
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/** Радиус круглой ripple-вспышки при нажатии на таб (шире — крупнее круг). */
private val TabRippleRadius = 68.dp

/** Сдвиг контента таба (иконка+подпись) вниз внутри бара. Больше — ниже (риск подреза подписи). */
private val TabContentVerticalOffset = 5.dp

/**
 * Нижняя панель навигации по корневым вкладкам [TopLevelDestination].
 *
 * Insets обрабатываются здесь один раз (внешний [Box] с [navigationBarsPadding] +
 * [NavigationBar] с обнулёнными insets) — экраны их не трогают.
 *
 * Элементы — ручные: официальный [androidx.compose.material3.NavigationBarItem] не даёт
 * заменить свой ripple (узкий, по форме пилюли), а нам нужна круглая широкая вспышка без
 * постоянного индикатора. Чтобы при этом не терять доступность, состояние вкладки задаём
 * через [selectable] с [Role.Tab] (озвучка «выбрано / вкладка»), а группировку даёт сам
 * [NavigationBar] (внутри `selectableGroup`).
 */
@Composable
fun Grammar8rBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (TopLevelDestination) -> Unit,
) {
    Box(
        modifier = Modifier
            .background(CardBackground)
            .navigationBarsPadding(),
    ) {
        NavigationBar(
            containerColor = CardBackground,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.height(57.dp),
        ) {
            TopLevelDestination.entries.forEach { destination ->
                val selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(destination.route::class)
                } == true
                val label = stringResource(destination.labelRes)
                val tint = if (selected) Accent else TextSecondary
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = selected,
                            onClick = { onNavigate(destination) },
                            role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, radius = TabRippleRadius),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = TabContentVerticalOffset),
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = label,
                            tint = tint,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(text = label, color = tint, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}