package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseContentText
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseInputField
import dev.sethan8r.grammar.app.ui.components.exercise.parts.InputFieldVisual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.TableFillRow
import dev.sethan8r.grammar.app.domain.usecase.AnswerNormalizer
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Рендерер TABLE_FILL во [ExerciseFrame]: задание + строки «подсказка → поле ввода». Поле —
 * общий [ExerciseInputField] (Правило №0). Вердикт all-or-nothing: на реванше ([AnswerPhase.REVEALED])
 * верные ячейки — зелёная рамка, неверные — красная + правильный ответ под строкой. На первой ошибке
 * НИЧЕГО не подсвечиваем (пользователь сам ищет ошибку) — это держит фаза (до REVEALED — нейтрально).
 */
@Composable
fun TableFillExerciseView(
    exercise: Exercise.TableFill,
    answer: ExerciseAnswer.TextAnswers?,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onChange: (rowIndex: Int, value: String) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
        ) {
            exercise.rows.forEachIndexed { index, row ->
                val input = inputs.getOrElse(index) { "" }
                val visual = visualFor(phase, row, input)
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                    ) {
                        ExerciseContentText(
                            text = row.hint,
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                        )
                        ExerciseInputField(
                            value = input,
                            enabled = editable,
                            onValueChange = { onChange(index, it) },
                            visual = visual,
                            modifier = Modifier
                                .width(Dimens.tableCellMinWidth)
                                .height(44.dp),
                        )
                    }
                    if (visual == InputFieldVisual.WRONG) {
                        Text(
                            text = stringResource(R.string.exercise_correct_answer, row.answer),
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

private fun visualFor(phase: AnswerPhase, row: TableFillRow, input: String): InputFieldVisual = when {
    phase != AnswerPhase.REVEALED -> InputFieldVisual.NEUTRAL
    AnswerNormalizer.matches(row.answer, input) -> InputFieldVisual.CORRECT
    else -> InputFieldVisual.WRONG
}