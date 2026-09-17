package dev.sethan8r.grammar.app.ui.components.clarify

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Готовые вопросы карточки — ОДНА реплика пользователя, внутри которой каждый вопрос лежит своим
 * фреймом. Тап по фрейму отправляет вопрос, и реплика становится обычным сообщением с одним
 * выбранным вопросом. Вернуть выбор можно кнопкой «Задать другой вопрос».
 */
@Composable
fun ClarifyQuestionOptions(
    options: List<String>,
    enabled: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ClarifyUserMessage(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        ) {
            options.forEach { option ->
                MarkdownText(
                    text = option,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.cornerButton))
                        .background(Background.copy(alpha = Alphas.optionFrame))
                        .clickable(enabled = enabled) { onSelect(option) }
                        .padding(Dimens.spaceMedium),
                    color = Background,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}
