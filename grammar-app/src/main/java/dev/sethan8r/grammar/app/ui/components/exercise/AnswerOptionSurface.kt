package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed

/**
 * Визуальное состояние выбираемого варианта/утверждения — общий словарь цветов для заданий выбора
 * одного варианта (SingleSelect) и multi-select (TRUE_FALSE). Один источник правды (Правило №0).
 */
enum class AnswerOptionVisual { NORMAL, SELECTED, CORRECT, WRONG_PICK }

/**
 * Кликабельная «карточка-вариант» ответа: рамка + подкрашенный по [visual] центр (NORMAL — нейтральная
 * обводка без заливки; остальные — заливка центра тем же цветом с прозрачностью [Alphas.answerFill]).
 * Содержимое (текст/строки) задаёт вызывающий слотом [content]. Используется единым рендерером
 * выбора варианта и заданием TRUE_FALSE — внешний вид варианта живёт ровно один раз.
 */
@Composable
fun AnswerOptionSurface(
    visual: AnswerOptionVisual,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val accentColor = when (visual) {
        AnswerOptionVisual.NORMAL -> Inactive
        AnswerOptionVisual.SELECTED -> Accent
        AnswerOptionVisual.CORRECT -> CorrectGreen
        AnswerOptionVisual.WRONG_PICK -> IncorrectRed
    }
    val fillColor = if (visual == AnswerOptionVisual.NORMAL) Background else accentColor.copy(alpha = Alphas.answerFill)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerButton))
            .background(fillColor)
            .border(2.dp, accentColor, RoundedCornerShape(Dimens.cornerButton))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(Dimens.cardPadding),
        content = content,
    )
}