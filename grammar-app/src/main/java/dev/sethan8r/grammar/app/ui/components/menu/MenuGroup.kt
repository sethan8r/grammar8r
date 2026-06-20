package dev.sethan8r.grammar.app.ui.components.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/** Один пункт меню в [MenuGroup]: иконка, подпись и действие по тапу. */
data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

/**
 * Секция меню (iOS-style): заголовок над общим фреймом + пункты [items] внутри одного скруглённого
 * блока, разделённые тонкими линиями (разделитель выровнен под подпись, не доходит до иконки).
 * Группировка пунктов — единственный источник раскладки экрана меню (Правило №0).
 */
@Composable
fun MenuGroup(
    title: String,
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            modifier = Modifier.padding(start = Dimens.spaceTiny, bottom = Dimens.spaceSmall),
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.cornerCard))
                .background(CardBackground),
        ) {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = MenuRowLabelInset),
                        color = Inactive,
                    )
                }
                MenuRow(item)
            }
        }
    }
}

/** Левый отступ подписи (иконка + зазор) — под него выравнивается разделитель между пунктами. */
private val MenuRowLabelInset = Dimens.cardPadding + Dimens.spaceXLarge + Dimens.spaceMedium

@Composable
private fun MenuRow(item: MenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = Dimens.cardPadding)
            .height(Dimens.buttonHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(Dimens.spaceXLarge),
        )
        Spacer(Modifier.width(Dimens.spaceMedium))
        Text(
            text = item.label,
            modifier = Modifier.weight(1f),
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(Dimens.spaceXLarge),
        )
    }
}