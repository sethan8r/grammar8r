package dev.sethan8r.grammar.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Кнопка «i» в кружке. По тапу вызывает [onClick] — экран показывает описание нижним снекбаром
 * ([FeedbackSnackbarController], семантика «заменять, а не копить»: тыкая по разным «i», описания
 * сменяют друг друга, а не выстраиваются в очередь). Сама ничего не рендерит сверх иконки.
 * Используется там, где описание скрыто (раздел, тема).
 */
@Composable
fun InfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(Dimens.spaceXXLarge),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(R.string.info_show),
            tint = TextSecondary,
        )
    }
}