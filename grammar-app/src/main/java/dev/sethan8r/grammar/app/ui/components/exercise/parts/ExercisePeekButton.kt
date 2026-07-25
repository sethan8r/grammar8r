package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.Accent

/** Зона тапа глазка — компактнее штатных 48dp кнопки-иконки, чтобы не отжимать поле ответа. */
private val PEEK_BUTTON = 32.dp
private val PEEK_ICON = 20.dp

/**
 * Глазок «показать правильный ответ ↔ вернуть свой» у ошибочного задания на реванше (Правило №0:
 * один вид и одно поведение для TEXT_INPUT, TRANSFORMATION и WORD_ARRANGEMENT). Сам глазок ничего не
 * знает про ответ — он только переключает [peeking], а подмену содержимого делает задание.
 *
 * Ripple выключен (как у остальных тапов в заданиях). Зона тапа — минимум [PEEK_BUTTON]; инлайн-вставке
 * в предложение передают `fillMaxSize()`, и тогда размер держит слот.
 */
@Composable
fun ExercisePeekButton(peeking: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = PEEK_BUTTON, minHeight = PEEK_BUTTON)
            .clickable(interactionSource = interaction, indication = null, onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (peeking) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            contentDescription = stringResource(
                if (peeking) R.string.exercise_peek_own else R.string.exercise_peek_correct,
            ),
            tint = Accent,
            modifier = Modifier.size(PEEK_ICON),
        )
    }
}
