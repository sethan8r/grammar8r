package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Рендерер TEXT_INPUT: 1–5 пунктов с вводом ответа, во [ExerciseFrame] (трясётся на ошибке). При показе
 * правильного (после двух ошибок) ввод пользователя остаётся в поле (видит свой вариант), под ним —
 * правильный ответ зелёным, затем объяснение. Единой шапки нет → верхней линии-разделителя тоже нет.
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
                    MarkdownText(text = item.sentence, fontSize = 18.sp)
                    if (item.contextRu.isNotBlank()) {
                        MarkdownText(text = item.contextRu, color = TextSecondary, fontSize = 14.sp)
                    }
                    OutlinedTextField(
                        value = inputs.getOrElse(index) { "" },
                        onValueChange = { onChange(index, it) },
                        enabled = editable,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
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