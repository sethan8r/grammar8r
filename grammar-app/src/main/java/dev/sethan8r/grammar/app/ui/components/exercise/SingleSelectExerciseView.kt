package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.domain.model.exercise.Option
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed

/** Визуальное состояние варианта ответа. */
private enum class OptionVisual { NORMAL, SELECTED, CORRECT, WRONG_PICK }

/**
 * Единый рендерер заданий с выбором одного варианта ([dev.sethan8r.grammar.app.domain.model.exercise.Exercise.SingleSelect]):
 * MULTIPLE_CHOICE / FORWARD_CHOICE / REVERSE_CHOICE / ERROR_CORRECTION / CONSTRUCTION_MEANING /
 * DIALOG_RESTORE / FIND_THE_ODD. Внутри [ExerciseFrame]: шапка-условие (слот [header]) →
 * линия-разделитель от края до края → варианты → объяснение на реванше. Типы отличаются ТОЛЬКО
 * содержимым шапки (Правило №0) — её даёт вызывающий через [SingleSelectHeader].
 */
@Composable
fun SingleSelectExerciseView(
    options: List<Option>,
    selectedIndex: Int,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    explanation: String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
) {
    // Все правильные варианты (db_schema допускает 2+ у ErrorCorrection) — на реванше подсвечиваем зелёным все.
    val correctIndices = options.indices.filter { options[it].isCorrect }.toSet()
    val editable = phase.isEditable

    ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey, modifier = modifier) {
        // Шапка-условие (с горизонтальным отступом — разделитель ниже идёт от края до края).
        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny),
            content = { header() },
        )

        // Разделитель «шапка ↔ варианты» (единый компонент, от края до края фрейма).
        ExerciseDivider()

        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
        ) {
            options.forEachIndexed { index, option ->
                OptionRow(
                    text = option.text,
                    visual = visualFor(phase, index, selectedIndex, correctIndices),
                    enabled = editable,
                    onClick = { onSelect(index) },
                )
            }
        }

        ExerciseExplanation(phase = phase, text = explanation)
    }
}

private fun visualFor(
    phase: AnswerPhase,
    index: Int,
    selected: Int,
    correctIndices: Set<Int>,
): OptionVisual = when (phase) {
    AnswerPhase.ANSWERING, AnswerPhase.WRONG_FIRST ->
        if (index == selected) OptionVisual.SELECTED else OptionVisual.NORMAL
    AnswerPhase.CORRECT ->
        if (index == selected) OptionVisual.CORRECT else OptionVisual.NORMAL
    AnswerPhase.REVEALED -> when {
        index in correctIndices -> OptionVisual.CORRECT
        index == selected -> OptionVisual.WRONG_PICK
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