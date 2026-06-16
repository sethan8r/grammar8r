package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.TextItem
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

private const val BLANK_ID = "blank"

/**
 * Рендерер TEXT_INPUT во [ExerciseFrame] (трясётся на ошибке). Пропуск `___` — это инлайн-поле ввода
 * прямо в предложении (пользователь печатает в него, а не в отдельное поле), ширина — по длине
 * правильного ответа. На реванше ввод пользователя остаётся в поле, ниже — правильный ответ + объяснение.
 * Проверка ввода — case-insensitive с учётом сокращений ([dev.sethan8r.grammar.app.domain.usecase.AnswerNormalizer]).
 */
@Composable
fun TextInputExerciseView(
    exercise: Exercise.TextInput,
    answer: ExerciseAnswer.TextAnswers?,
    phase: AnswerPhase,
    shakeKey: Int,
    onChange: (itemIndex: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase == AnswerPhase.ANSWERING || phase == AnswerPhase.WRONG_FIRST
    val inputs = answer?.inputs.orEmpty()

    ExerciseFrame(shakeKey = shakeKey, modifier = modifier) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge),
        ) {
            exercise.items.forEachIndexed { index, item ->
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny)) {
                    SentenceWithBlank(
                        item = item,
                        value = inputs.getOrElse(index) { "" },
                        enabled = editable,
                        onValueChange = { onChange(index, it) },
                    )
                    if (item.contextRu.isNotBlank()) {
                        Text(item.contextRu, color = TextSecondary, fontSize = 14.sp, fontStyle = FontStyle.Italic)
                    }
                    if (phase == AnswerPhase.REVEALED) {
                        Text(
                            text = stringResource(R.string.exercise_correct_answer, item.answer),
                            color = CorrectGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
            if (phase == AnswerPhase.REVEALED && exercise.explanation.isNotBlank()) {
                MarkdownText(text = exercise.explanation, color = TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

/** Предложение с инлайн-полем на месте `___`; ширина поля — по длине правильного ответа. */
@Composable
private fun SentenceWithBlank(
    item: TextItem,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
) {
    val annotated = buildAnnotatedString {
        val marker = item.sentence.indexOf("___")
        if (marker >= 0) {
            append(item.sentence.substring(0, marker))
            appendInlineContent(BLANK_ID, " ")
            var end = marker
            while (end < item.sentence.length && item.sentence[end] == '_') end++
            append(item.sentence.substring(end))
        } else {
            append(item.sentence)
            append(" ")
            appendInlineContent(BLANK_ID, " ")
        }
    }
    // Ширина поля растёт от длины ожидаемого ответа (em — тянется за шрифтом).
    val widthEm = (item.answer.length.coerceAtLeast(3) * 0.62f + 1.6f).em
    val inlineContent = mapOf(
        BLANK_ID to InlineTextContent(
            placeholder = Placeholder(
                width = widthEm,
                height = 1.7.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
            ),
        ) {
            BlankInputField(value = value, enabled = enabled, onValueChange = onValueChange)
        },
    )
    Text(
        text = annotated,
        inlineContent = inlineContent,
        color = TextPrimary,
        fontSize = 20.sp,
        lineHeight = 34.sp,
    )
}

/** Поле ввода в пропуске: свой фрейм (рамка), ввод по центру, курсор-акцент. */
@Composable
private fun BlankInputField(value: String, enabled: Boolean, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(Dimens.cornerSmall))
            .background(Background)
            .border(1.dp, Inactive, RoundedCornerShape(Dimens.cornerSmall)),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(color = TextPrimary, fontSize = 18.sp, textAlign = TextAlign.Center),
            cursorBrush = SolidColor(Accent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceTiny),
        )
    }
}