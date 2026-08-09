package dev.sethan8r.grammar.app.ui.components.clarify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Поле свободного вопроса к ИИ с круглой кнопкой отправки. В отличие от полей хардкод-заданий
 * ([dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseInputField]) подсказки клавиатуры
 * здесь НЕ глушим: пользователь пишет обычный русский вопрос, а не ответ, который можно подсмотреть.
 *
 * Длину ограничивает вызывающий (сервер принимает не больше 300 символов).
 */
@Composable
fun ClarifyInputBar(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Одна строка — капсула; со второй строки поле становится прямоугольником со скруглением.
    // Кнопка всегда в одном ряду с текстом у нижнего края: строки переносятся, не доходя до неё,
    // поэтому последняя строка встаёт рядом с кнопкой и пустой полосы справа не остаётся.
    var lineCount by remember { mutableIntStateOf(1) }
    val isMultiline = lineCount > 1
    val shape = if (isMultiline) RoundedCornerShape(Dimens.cornerLarge) else CircleShape

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBackground)
            .border(Dimens.outlineWidth, Inactive, shape)
            .padding(Dimens.spaceSmall),
        verticalAlignment = Alignment.Bottom,
    ) {
        // Высота поля не ниже кнопки отправки, текст по её центру — иначе однострочный ввод
        // «провисает» под серединой капсулы.
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = Dimens.sendButtonSize)
                .padding(horizontal = Dimens.spaceSmall),
            contentAlignment = Alignment.CenterStart,
        ) {
            // Плейсхолдер и ввод рисуются одним стилем — иначе текст «прыгает» при первом символе.
            if (value.isEmpty()) {
                Text(
                    text = stringResource(R.string.clarify_input_placeholder),
                    color = TextSecondary,
                    style = inputTextStyle,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                maxLines = MAX_INPUT_LINES,
                textStyle = inputTextStyle.copy(color = TextPrimary),
                cursorBrush = SolidColor(Accent),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default,
                ),
                onTextLayout = { lineCount = it.lineCount },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        SendButton(enabled = enabled && value.isNotBlank(), onClick = onSend)
    }
}

@Composable
private fun SendButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(Dimens.sendButtonSize),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Accent,
            contentColor = Background,
            disabledContainerColor = Inactive,
            disabledContentColor = TextSecondary,
        ),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = stringResource(R.string.clarify_send),
            modifier = Modifier.size(Dimens.sendButtonGlyph),
        )
    }
}

/** Сколько строк максимум растёт поле, прежде чем начать скроллиться внутри себя. */
private const val MAX_INPUT_LINES = 4

/** Общий стиль плейсхолдера и вводимого текста — они лежат друг на друге и должны совпадать. */
private val inputTextStyle = TextStyle(fontSize = 16.sp, lineHeight = 22.sp)
