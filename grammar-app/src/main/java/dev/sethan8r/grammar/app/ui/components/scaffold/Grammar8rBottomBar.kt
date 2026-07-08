package dev.sethan8r.grammar.app.ui.components.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.sethan8r.grammar.app.ui.navigation.TopLevelDestination
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/** Доля ширины активного (растущего) чипа относительно неактивного таба [InactiveTabWeight]. */
private const val ActiveChipWeight = 2.4f

/** Доля ширины неактивного таба (только иконка). */
private const val InactiveTabWeight = 1f

/**
 * Плавающая нижняя навигация по корневым вкладкам [TopLevelDestination] — капсула (вариант 2a).
 *
 * Капсула фиксированной ширины (полноширинная с плавающими отступами), парит над контентом
 * (рисуется оверлеем в MainScreen, НЕ в `Scaffold.bottomBar`). Неактивный таб — только иконка равной
 * доли; активный анимированно растёт в залитый акцентом чип с подписью, соседи сужаются (анимируется
 * `weight`). Высота капсулы закреплена ([Dimens.bottomBarChipHeight]) — не дёргается по вертикали при
 * переключении. Insets низа держит сама ([navigationBarsPadding] + плавающий зазор).
 *
 * Элементы ручные (не [androidx.compose.material3.NavigationBarItem]): нужен свой растущий чип.
 * Доступность — через [selectable] с [Role.Tab].
 */
@Composable
fun Grammar8rBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (TopLevelDestination) -> Unit,
) {
    val containerShape = RoundedCornerShape(Dimens.bottomBarContainerRadius)
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(
                horizontal = Dimens.bottomBarFloatingMargin,
                vertical = Dimens.bottomBarFloatingBottomGap,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Равномерный ореол-тень: чёрный слой формы капсулы, размытый наружу (Unbounded) — даёт кольцо
        // «чёрный у края → прозрачный» одинаковой толщины со всех сторон. Капсула сверху перекрывает центр.
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(Dimens.bottomBarHaloBlur, BlurredEdgeTreatment.Unbounded)
                .background(Color.Black.copy(alpha = Alphas.bottomBarHalo), containerShape),
        )
        Row(
            modifier = Modifier
                .clip(containerShape)
                .background(CardBackground)
                .padding(Dimens.bottomBarContainerPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimens.bottomBarChipGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopLevelDestination.entries.forEach { destination ->
                val selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(destination.route::class)
                } == true
                val weight by animateFloatAsState(
                    targetValue = if (selected) ActiveChipWeight else InactiveTabWeight,
                    animationSpec = tween(Durations.bottomBarChipGrowMs, easing = FastOutSlowInEasing),
                    label = "chipWeight",
                )
                NavTab(
                    destination = destination,
                    selected = selected,
                    onClick = { onNavigate(destination) },
                    modifier = Modifier.weight(weight),
                )
            }
        }
    }
}

/**
 * Один таб капсулы: неактивный — центрированная иконка; активный — залитый акцентом чип с иконкой +
 * подписью (проявляется по мере роста ширины таба). Высота фиксирована [Dimens.bottomBarChipHeight],
 * поэтому появление подписи не меняет высоту ряда (нет вертикального дёрганья). Фидбек нажатия —
 * только смена цвета/подписи (без ripple и press-scale).
 */
@Composable
private fun NavTab(
    destination: TopLevelDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(destination.labelRes)
    val chipShape = RoundedCornerShape(Dimens.bottomBarChipRadius)
    val contentColor = if (selected) Background else TextSecondary

    Row(
        modifier = modifier
            .height(Dimens.bottomBarChipHeight)
            .clip(chipShape)
            .background(if (selected) Accent else Color.Transparent)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            )
            .padding(horizontal = Dimens.bottomBarChipHorizontalPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(Dimens.bottomBarIconSize),
        )
        AnimatedVisibility(
            visible = selected,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            Text(
                text = label,
                color = Background,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(start = Dimens.bottomBarChipGap),
            )
        }
    }
}