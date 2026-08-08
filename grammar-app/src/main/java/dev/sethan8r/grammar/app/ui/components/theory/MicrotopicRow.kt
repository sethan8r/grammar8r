package dev.sethan8r.grammar.app.ui.components.theory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicState
import dev.sethan8r.grammar.app.domain.model.theory.MicrotopicSummary
import dev.sethan8r.grammar.app.ui.components.DualTitle
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive

/** Чем показан статус прохождения микротемы: полосой у края карточки или точкой в строке списка. */
enum class MicrotopicStatusStyle { STRIPE, DOT }

/**
 * Строка микротемы с индикатором прохождения. Галочки нет — статус несёт цвет индикатора:
 * [CorrectGreen] у пройденной, [Inactive] у остальных.
 *
 * На экране темы каждая строка это отдельная карточка, поэтому статус рисуется полосой во всю
 * высоту; в выдаче поиска строки лежат внутри общей панели, и там уместна точка.
 */
@Composable
fun MicrotopicRow(
    microtopic: MicrotopicSummary,
    onMicrotopicClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    statusStyle: MicrotopicStatusStyle = MicrotopicStatusStyle.STRIPE,
) {
    val statusColor =
        if (microtopic.state == MicrotopicState.COMPLETED) CorrectGreen else Inactive
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onMicrotopicClick(microtopic.id) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (statusStyle) {
            MicrotopicStatusStyle.STRIPE -> Box(
                modifier = Modifier
                    .width(Dimens.microtopicStripeWidth)
                    .fillMaxHeight()
                    .background(statusColor),
            )

            MicrotopicStatusStyle.DOT -> Box(
                modifier = Modifier
                    .padding(start = Dimens.spaceSmall)
                    .size(Dimens.statusDot)
                    .clip(CircleShape)
                    .background(statusColor),
            )
        }
        DualTitle(
            title = microtopic.title,
            modifier = Modifier
                .weight(1f)
                .padding(Dimens.cardPadding),
        )
    }
}
