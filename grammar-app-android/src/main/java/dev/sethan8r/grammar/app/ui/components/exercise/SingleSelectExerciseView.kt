package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.AnswerOptionSurface
import dev.sethan8r.grammar.app.ui.components.exercise.parts.AnswerOptionVisual
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseContentText
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.core.model.exercise.Option
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Dimens

/**
 * Единый рендерер заданий с выбором одного варианта ([dev.sethan8r.grammar.core.model.exercise.Exercise.SingleSelect]):
 * MULTIPLE_CHOICE / FORWARD_CHOICE / REVERSE_CHOICE / ERROR_CORRECTION / CONSTRUCTION_MEANING /
 * DIALOG_RESTORE / FIND_THE_ODD. Внутри [ExerciseFrame]: шапка-условие (слот [header]) →
 * линия-разделитель от края до края → варианты ([AnswerOptionSurface]) → объяснение на реванше. Типы
 * отличаются ТОЛЬКО содержимым шапки (Правило №0) — её даёт вызывающий через [SingleSelectHeader].
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
                AnswerOptionSurface(
                    visual = visualFor(phase, index, selectedIndex, correctIndices),
                    enabled = editable,
                    onClick = { onSelect(index) },
                ) {
                    ExerciseContentText(text = option.text, fontSize = 16.sp)
                }
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
): AnswerOptionVisual = when (phase) {
    AnswerPhase.ANSWERING, AnswerPhase.WRONG_FIRST ->
        if (index == selected) AnswerOptionVisual.SELECTED else AnswerOptionVisual.NORMAL
    AnswerPhase.CORRECT ->
        if (index == selected) AnswerOptionVisual.CORRECT else AnswerOptionVisual.NORMAL
    AnswerPhase.REVEALED -> when {
        index in correctIndices -> AnswerOptionVisual.CORRECT
        index == selected -> AnswerOptionVisual.WRONG_PICK
        else -> AnswerOptionVisual.NORMAL
    }
}