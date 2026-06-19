package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.components.MarkdownText
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Блок объяснения после слитых попыток (фаза [AnswerPhase.REVEALED]): линия-разделитель от края до
 * края фрейма + текст правила. Единый для всех типов заданий (Правило №0) — вызывается прямым
 * потомком `ColumnScope` [ExerciseFrame] и сам ничего не рисует, пока ответ не показан / текст пуст.
 */
@Composable
fun ExerciseExplanation(phase: AnswerPhase, text: String, modifier: Modifier = Modifier) {
    if (phase != AnswerPhase.REVEALED || text.isBlank()) return
    ExerciseDivider()
    MarkdownText(
        text = text,
        modifier = modifier.padding(horizontal = Dimens.cardPadding),
        color = TextSecondary,
        fontSize = 14.sp,
    )
}