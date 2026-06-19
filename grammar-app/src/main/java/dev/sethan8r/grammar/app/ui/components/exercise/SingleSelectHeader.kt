package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.domain.model.exercise.DialogLine
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Шапка-условие задания с выбором варианта. Единственное, чем отличаются типы [Exercise.SingleSelect]
 * друг от друга — диспетчеризуется здесь, тело каждого типа — отдельная приватная шапка ниже.
 * Контентный EN — через [MarkdownText]; инструкции/контекст на RU — приглушённо.
 */
@Composable
fun SingleSelectHeader(exercise: Exercise.SingleSelect) {
    when (exercise) {
        is Exercise.Choice -> ChoiceHeader(exercise)
        is Exercise.ErrorCorrection -> StatementHeader(exercise.wrongSentence)
        is Exercise.ConstructionMeaning -> StatementHeader(exercise.construction)
        is Exercise.DialogRestore -> DialogHeader(exercise.lines)
        is Exercise.FindTheOdd -> InstructionHeader(exercise.groupDescription)
    }
}

/** CHOICE/FORWARD/REVERSE: предложение-условие (с пропусками) + опц. русский контекст. */
@Composable
private fun ChoiceHeader(exercise: Exercise.Choice) {
    MarkdownText(text = exercise.prompt, fontSize = 18.sp, renderBlanks = true)
    if (exercise.contextRu.isNotBlank()) {
        MarkdownText(text = exercise.contextRu, color = TextSecondary, fontSize = 14.sp)
    }
}

/** ERROR_CORRECTION / CONSTRUCTION_MEANING: одно EN-предложение/конструкция-условие. */
@Composable
private fun StatementHeader(text: String) {
    MarkdownText(text = text, fontSize = 18.sp)
}

/** FIND_THE_ODD: русская инструкция «что объединяет три из четырёх». */
@Composable
private fun InstructionHeader(text: String) {
    MarkdownText(text = text, color = TextPrimary, fontSize = 16.sp)
}

/** DIALOG_RESTORE: реплики диалога; пропуск (`text == null`) — полоска на месте восстанавливаемой реплики. */
@Composable
private fun DialogHeader(lines: List<DialogLine>) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        lines.forEach { line ->
            val isBlank = line.text == null
            Row(verticalAlignment = if (isBlank) Alignment.Bottom else Alignment.Top) {
                Text(
                    text = "${line.speaker}:",
                    color = Accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(Dimens.spaceSmall))
                if (line.text != null) {
                    MarkdownText(text = line.text, fontSize = 18.sp, modifier = Modifier.weight(1f))
                } else {
                    // Полоска-пропуск фиксированной длины; чуть приподнята от низа строки (на уровень базовой линии).
                    BlankBar(modifier = Modifier.padding(bottom = Dimens.spaceTiny))
                }
            }
        }
    }
}