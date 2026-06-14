package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive

/**
 * Сегментированная полоса прогресса карточек: [total] делений, первые [filledCount] закрашены
 * акцентом, остальные — неактивным. Заменяет текстовый счётчик «X из N».
 */
@Composable
fun SegmentedProgressBar(
    total: Int,
    filledCount: Int,
    modifier: Modifier = Modifier,
) {
    if (total <= 0) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.progressBarHeight)
                    .clip(RoundedCornerShape(Dimens.progressBarHeight))
                    .background(if (index < filledCount) Accent else Inactive),
            )
        }
    }
}