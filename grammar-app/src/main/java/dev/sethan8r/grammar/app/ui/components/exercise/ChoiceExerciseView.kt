package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/** Визуальное состояние варианта ответа. */
private enum class OptionVisual { NORMAL, SELECTED, CORRECT, WRONG_PICK }

/**
 * Рендерер упражнений с выбором варианта (MULTIPLE_CHOICE / FORWARD_CHOICE / REVERSE_CHOICE — один
 * UI, см. exercise_templates.md). На реализации F2 (ErrorCorrection и др.) сядет этот же компонент.
 * Внутри [ExerciseFrame]: шапка-условие → линия-разделитель от края до края → варианты →
 * объяснение на реванше. Контентный текст — через [MarkdownText].
 */
@Composable
fun ChoiceExerciseView(
    exercise: Exercise.Choice,
    answer: ExerciseAnswer.SingleChoice?,
    phase: AnswerPhase,
    shakeKey: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = answer?.selectedIndex ?: -1
    val correctIndex = exercise.options.indexOfFirst { it.isCorrect }

    ExerciseFrame(shakeKey = shakeKey, modifier = modifier) {
        // Шапка-условие (с горизонтальным отступом — разделитель ниже идёт от края до края).
        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
        ) {
            MarkdownText(text = exercise.prompt, fontSize = 18.sp)
            if (exercise.contextRu.isNotBlank()) {
                MarkdownText(text = exercise.contextRu, color = TextSecondary, fontSize = 14.sp)
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimens.cardPadding),
            color = Inactive,
        )

        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
        ) {
            exercise.options.forEachIndexed { index, option ->
                OptionRow(
                    text = option.text,
                    visual = visualFor(phase, index, selected, correctIndex),
                    enabled = phase == AnswerPhase.ANSWERING || phase == AnswerPhase.WRONG_FIRST,
                    onClick = { onSelect(index) },
                )
            }
            if (phase == AnswerPhase.REVEALED && exercise.explanation.isNotBlank()) {
                MarkdownText(
                    text = exercise.explanation,
                    color = TextSecondary,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

private fun visualFor(phase: AnswerPhase, index: Int, selected: Int, correctIndex: Int): OptionVisual = when (phase) {
    AnswerPhase.ANSWERING, AnswerPhase.WRONG_FIRST ->
        if (index == selected) OptionVisual.SELECTED else OptionVisual.NORMAL
    AnswerPhase.CORRECT ->
        if (index == selected) OptionVisual.CORRECT else OptionVisual.NORMAL
    AnswerPhase.REVEALED -> when (index) {
        correctIndex -> OptionVisual.CORRECT
        selected -> OptionVisual.WRONG_PICK
        else -> OptionVisual.NORMAL
    }
}

@Composable
private fun OptionRow(text: String, visual: OptionVisual, enabled: Boolean, onClick: () -> Unit) {
    val borderColor = when (visual) {
        OptionVisual.NORMAL -> Inactive
        OptionVisual.SELECTED -> Accent
        OptionVisual.CORRECT -> CorrectGreen
        OptionVisual.WRONG_PICK -> IncorrectRed
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerButton))
            .background(Background)
            .border(2.dp, borderColor, RoundedCornerShape(Dimens.cornerButton))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(Dimens.cardPadding),
    ) {
        MarkdownText(text = text, fontSize = 16.sp)
    }
}