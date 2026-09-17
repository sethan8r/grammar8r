package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseAccordionItem
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseContentText
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseInputField
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExercisePeekButton
import dev.sethan8r.grammar.app.ui.components.exercise.parts.InputFieldVisual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.core.model.exercise.Exercise
import dev.sethan8r.grammar.core.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.core.model.exercise.TableFillRow
import dev.sethan8r.grammar.core.usecase.AnswerNormalizer
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.isImeVisible

/** Минимальная высота поля ответа (одна строка 18sp + воздух). */
private val FIELD_MIN_HEIGHT = 48.dp

/** Ответ занимает до двух строк: не влез в ширину поля — переносится сам, поле подрастает. */
private const val FIELD_LINES = 2

/**
 * Рендерер TABLE_FILL во [ExerciseFrame]: задание + строки «подсказка → поле ввода». Поле — общий
 * [ExerciseInputField] (Правило №0), проверка через [AnswerNormalizer].
 *
 * Раскладка — аккордеон-фокус на общем [ExerciseAccordionItem]: раскрыта ровно ОДНА строка (подсказка,
 * стрелка, поле во всю ширину), остальные свёрнуты в «точка-индикатор + подсказка → вписанное жирным»
 * (пока не вписано — линия-пропуск). Тап по свёрнутой строке переводит фокус на неё; если ввод уже идёт,
 * клавиатура при переключении остаётся на месте. Поле во всю ширину держит ответ из нескольких слов,
 * а таблица из 7 строк не растёт в простыню. Когда всё верно, акцент строк становится зелёным.
 *
 * Вердикт all-or-nothing: на реванше ([AnswerPhase.REVEALED]) раскрыты все строки, верное поле зелёное,
 * неверное красное; у ошибочной строки появляется глазок, который подменяет содержимое поля эталоном
 * и обратно. На первой ошибке НИЧЕГО не подсвечиваем (пользователь сам ищет ошибку) — это держит фаза.
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
    val revealed = phase == AnswerPhase.REVEALED
    // Акцент задания: пока отвечают — синий, когда всё верно — зелёный (точки и рамка строки).
    val accent = if (phase == AnswerPhase.CORRECT) CorrectGreen else Accent
    var focusedIndex by rememberSaveable { mutableIntStateOf(0) }
    val imeVisible = isImeVisible()

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
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        ) {
            exercise.rows.forEachIndexed { index, row ->
                val input = inputs.getOrElse(index) { "" }
                TableFillItemRow(
                    row = row,
                    value = input,
                    focused = revealed || index == focusedIndex,
                    visual = visualFor(phase, row, input),
                    accent = accent,
                    editable = editable,
                    grabKeyboard = editable && index == focusedIndex && imeVisible,
                    onValueChange = { onChange(index, it) },
                    onFocus = { focusedIndex = index },
                )
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

/**
 * Одна строка таблицы: свёрнута — строка-превью «подсказка → вписанное» (общий [ExerciseAccordionItem]),
 * раскрыта — подсказка, под ней стрелка, поле ответа и слот глазка ([ExercisePeekButton]) — слот занят
 * всегда, кнопка в нём появляется у ошибочной строки.
 */
@Composable
private fun TableFillItemRow(
    row: TableFillRow,
    value: String,
    focused: Boolean,
    visual: InputFieldVisual,
    accent: Color,
    editable: Boolean,
    grabKeyboard: Boolean,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
) {
    ExerciseAccordionItem(
        focused = focused,
        filled = value.isNotBlank(),
        accent = accent,
        outlined = visual == InputFieldVisual.NEUTRAL,
        grabKeyboard = grabKeyboard,
        preview = previewRow(row.hint, value),
        previewArrowAccent = true,
        onFocus = onFocus,
    ) { fieldFocus ->
        // Ошибочная строка на реванше: глазок подменяет в поле ответ пользователя правильным и обратно.
        var peekCorrect by remember { mutableStateOf(false) }
        val wrong = visual == InputFieldVisual.WRONG

        ExerciseContentText(text = row.hint, fontSize = 18.sp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = accent,
            )
            ExerciseInputField(
                value = if (wrong && peekCorrect) row.answer else value,
                enabled = editable,
                onValueChange = onValueChange,
                // Пока показан правильный ответ, поле зелёное — видно, что это эталон, а не твой ввод.
                visual = if (wrong && peekCorrect) InputFieldVisual.CORRECT else visual,
                maxLines = FIELD_LINES,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = FIELD_MIN_HEIGHT)
                    .focusRequester(fieldFocus),
            )
            // Слот глазка занят всегда (кнопка — только у ошибочной строки): ширина поля одинакова
            // до и после проверки, поэтому вердикт не переверстывает строку.
            ExercisePeekButton(
                visible = wrong,
                peeking = peekCorrect,
                onToggle = { peekCorrect = !peekCorrect },
            )
        }
    }
}

/**
 * Текст превью: «подсказка → вписанное жирным». Пока поле пустое, за стрелкой стоит пропуск — он
 * рисуется линией ([ExerciseContentText]), как место под ответ в остальных заданиях.
 */
private fun previewRow(hint: String, value: String): String {
    val typed = value.trim()
    return if (typed.isEmpty()) "$hint → ___" else "$hint → **$typed**"
}