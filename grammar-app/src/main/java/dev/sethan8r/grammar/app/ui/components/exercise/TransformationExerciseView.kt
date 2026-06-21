package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.TransformItem
import dev.sethan8r.grammar.app.domain.usecase.AnswerNormalizer
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import androidx.compose.ui.res.stringResource

/**
 * Рендерер TRANSFORMATION во [ExerciseFrame]: задание + ровно 3 примера «исходное → поле ввода
 * преобразованного». Поле — общий [ExerciseInputField] (Правило №0), проверка через [AnswerNormalizer].
 * Вердикт all-or-nothing: на реванше верное поле зелёное, неверное красное + правильный ответ под ним.
 */
@Composable
fun TransformationExerciseView(
    exercise: Exercise.Transformation,
    answer: ExerciseAnswer.TextAnswers?,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onChange: (itemIndex: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase.isEditable
    val inputs = answer?.inputs.orEmpty()

    ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey, modifier = modifier) {
        // Шапка-условие (с заглавной буквы — задаём в коде, чтобы не править контент).
        Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
            Text(
                text = exercise.taskDescription.replaceFirstChar { it.uppercase() },
                color = TextSecondary,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
            )
        }

        // Линия «шапка ↔ задание» (единый разделитель, как в заданиях выбора варианта).
        ExerciseDivider()

        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge),
        ) {
            exercise.items.forEachIndexed { index, item ->
                val input = inputs.getOrElse(index) { "" }
                val visual = visualFor(phase, item, input)
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny)) {
                    // Исходное предложение — контентный EN.
                    MarkdownText(text = item.original, fontSize = 18.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Accent,
                        )
                        ExerciseInputField(
                            value = input,
                            enabled = editable,
                            onValueChange = { onChange(index, it) },
                            visual = visual,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                        )
                    }
                    if (visual == InputFieldVisual.WRONG) {
                        Text(
                            text = stringResource(R.string.exercise_correct_answer, item.transformed),
                            color = CorrectGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }

        ExerciseExplanation(phase = phase, text = exercise.explanation)
    }
}

private fun visualFor(phase: AnswerPhase, item: TransformItem, input: String): InputFieldVisual = when {
    phase != AnswerPhase.REVEALED -> InputFieldVisual.NEUTRAL
    AnswerNormalizer.matches(item.transformed, input) -> InputFieldVisual.CORRECT
    else -> InputFieldVisual.WRONG
}