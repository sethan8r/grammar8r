package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Бокс с ID (карточки/упражнения) у полосы прогресса. Фиксированная ширина ([Dimens.idBadgeWidth]) —
 * чтобы 3-значные ID и «AI» не двигали полосу; текст по центру. [highlighted] = пройдено → зелёный.
 * Переиспользуется листалкой микротемы и сессией упражнений (правило №0).
 */
@Composable
fun IdBadge(text: String, highlighted: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(Dimens.idBadgeWidth)
            .clip(RoundedCornerShape(Dimens.cornerSmall))
            .background(if (highlighted) CorrectGreen else CardBackground)
            .padding(vertical = Dimens.spaceMicro),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (highlighted) TextPrimary else TextSecondary,
            fontSize = 12.sp,
            lineHeight = 12.sp,
            maxLines = 1,
            // Убираем «свинцовый» отступ шрифта — бокс по высоте облегает текст плотнее.
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
        )
    }
}