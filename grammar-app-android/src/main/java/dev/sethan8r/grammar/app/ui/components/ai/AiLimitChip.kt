package dev.sethan8r.grammar.app.ui.components.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.Highlight
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Остаток дневных запросов к ИИ — единственный вид этого счётчика в приложении (Правило №0):
 * шапка уточнения, умные задания, Практика. Тап ведёт на экран подписки и лимитов.
 *
 * Безлимитный тир рисует «∞»: показывать техническое число там нечего.
 */
@Composable
fun AiLimitChip(
    requestsLeft: Int,
    isUnlimited: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = if (isUnlimited) {
        stringResource(R.string.ai_limit_chip_unlimited)
    } else {
        pluralStringResource(R.plurals.ai_limit_chip, requestsLeft, requestsLeft)
    }
    val description = stringResource(R.string.ai_limit_chip_cd)
    // Запросы кончились — счётчик подсвечивается «маркером внимания», но остаётся спокойной подписью.
    val labelColor = if (!isUnlimited && requestsLeft <= 0) Highlight else TextSecondary

    Box(
        modifier = modifier
            .padding(end = Dimens.spaceSmall)
            .clip(CircleShape)
            .background(Elevated)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceTiny)
            .semantics { contentDescription = description },
    ) {
        Text(text = label, color = labelColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
