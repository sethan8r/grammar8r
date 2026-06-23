package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.AnswerOptionSurface
import dev.sethan8r.grammar.app.ui.components.exercise.parts.AnswerOptionVisual
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.ui.components.text.TranslatableText
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Рендерер TRUE_FALSE во [ExerciseFrame] — multi-select «отметьте верные» (НЕ тоггл ✓/✗): тап по
 * утверждению выделяет его (как выбор варианта), верно — когда отмечены РОВНО все истинные. Каждое
 * утверждение — EN-предложение ([TranslatableText]) + RU-перевод под ним. Условие («ОТМЕТЬТЕ ВЕРНЫЕ»)
 * показывает подпись типа НАД фреймом (`exerciseTypeLabel`), поэтому своей шапки тут нет. Вердикт
 * all-or-nothing: до [AnswerPhase.REVEALED] подсветки нет (1-я ошибка — анти-спойлер); на реванше
 * истинные — зелёные (это и есть верный набор), ложные отмеченные — красные. Выделение/цвета — общий
 * [AnswerOptionSurface] (Правило №0, один вид «карточки-варианта» с SingleSelect).
 */
@Composable
fun TrueFalseExerciseView(
    exercise: Exercise.TrueFalse,
    selectedIndices: Set<Int>,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase.isEditable

    ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey, modifier = modifier) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
        ) {
            exercise.statements.forEachIndexed { index, statement ->
                AnswerOptionSurface(
                    visual = visualFor(phase, statement.isTrue, index in selectedIndices),
                    enabled = editable,
                    onClick = { onToggle(index) },
                ) {
                    TranslatableText(text = statement.en, color = TextPrimary, fontSize = 16.sp)
                    Text(
                        text = statement.ru,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = Dimens.spaceTiny),
                    )
                }
            }
        }

        ExerciseExplanation(phase = phase, text = exercise.explanation)
    }
}

private fun visualFor(phase: AnswerPhase, isTrue: Boolean, selected: Boolean): AnswerOptionVisual = when (phase) {
    AnswerPhase.ANSWERING, AnswerPhase.WRONG_FIRST ->
        if (selected) AnswerOptionVisual.SELECTED else AnswerOptionVisual.NORMAL
    // Верно: пользователь отметил ровно истинные — выделенные подсвечиваем зелёным.
    AnswerPhase.CORRECT ->
        if (selected) AnswerOptionVisual.CORRECT else AnswerOptionVisual.NORMAL
    // Реванш: все истинные зелёным (верный набор виден целиком, включая пропущенные), ложные
    // ошибочно отмеченные — красным.
    AnswerPhase.REVEALED -> when {
        isTrue -> AnswerOptionVisual.CORRECT
        selected -> AnswerOptionVisual.WRONG_PICK
        else -> AnswerOptionVisual.NORMAL
    }
}