package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.sethan8r.grammar.app.ui.navigation.TopLevelDestination
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Нижняя панель навигации по корневым вкладкам [TopLevelDestination].
 *
 * Insets обрабатываются здесь один раз (внешний [Box] с [navigationBarsPadding] +
 * [NavigationBar] с обнулёнными insets) — экраны их не трогают.
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(top = 5.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, radius = 30.dp),
                        ) { onNavigate(destination) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.offset(y = 5.dp),
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = label,
                            tint = if (selected) Accent else TextSecondary,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = label,
                            color = if (selected) Accent else TextSecondary,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}
