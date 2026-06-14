package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private const val BUBBLE_AUTO_DISMISS_MS = 5_000L

/**
 * Кнопка «i» в кружке, по тапу показывающая описание во всплывающем облачке рядом с собой
 * (официальный [Popup]). Облачко исчезает через 5 секунд или по тапу (по нему / мимо). Аналог
 * инфо-кнопки в Words8r. Используется там, где описание скрыто (раздел, тема).
 */
@Composable
fun InfoBubble(
    description: String,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }

    // Облачко падает чуть ниже кнопки — на её высоту.
    val dropDownPx = with(LocalDensity.current) { Dimens.spaceXXLarge.roundToPx() }

    Box(modifier) {
        IconButton(
            onClick = { visible = true },
            modifier = Modifier.size(Dimens.spaceXXLarge),
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = stringResource(R.string.info_show),
                tint = TextSecondary,
            )
        }

        if (visible) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, dropDownPx),
                onDismissRequest = { visible = false },
                properties = PopupProperties(focusable = true),
            ) {
                LaunchedEffect(Unit) {
                    delay(BUBBLE_AUTO_DISMISS_MS)
                    visible = false
                }
                Box(
                    modifier = Modifier
                        .widthIn(max = Dimens.bubbleMaxWidth)
                        .clip(RoundedCornerShape(Dimens.cornerCard))
                        .background(CardBackground)
                        .clickable { visible = false }
                        .padding(Dimens.cardPadding),
                ) {
                    MarkdownText(text = description, color = TextPrimary, fontSize = 14.sp)
                }
            }
        }
    }
}