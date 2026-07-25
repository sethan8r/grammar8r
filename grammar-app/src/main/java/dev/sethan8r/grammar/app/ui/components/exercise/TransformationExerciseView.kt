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
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.TransformItem
import dev.sethan8r.grammar.app.domain.usecase.AnswerNormalizer
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.isImeVisible

/** Минимальная высота поля преобразованного предложения (одна строка 18sp + воздух). */
private val FIELD_MIN_HEIGHT = 48.dp

/** Ответ занимает до двух строк: не влез в ширину поля — переносится сам, поле подрастает. */
private const val FIELD_LINES = 2

/** Свёрнутому пункту отведено две строки: в одну «исходное → вписанное» не влезает. */
private const val PREVIEW_LINES = 2

/**
 * Рендерер TRANSFORMATION во [ExerciseFrame]: задание + ровно 3 примера «исходное → поле ввода
 * преобразованного». Поле — общий [ExerciseInputField] (Правило №0), проверка через [AnswerNormalizer].
 *
 * Раскладка — аккордеон-фокус на общем [ExerciseAccordionItem]: раскрыт ровно ОДИН пример (исходное,
 * стрелка, поле), остальные свёрнуты в строку «точка-индикатор + исходное → вписанное жирным» (пока
 * не вписано — линия-пропуск). Тап по свёрнутой строке переводит фокус на неё; если ввод уже идёт,
 * клавиатура при переключении остаётся на месте. Когда всё верно, акцент пунктов становится зелёным.
 *
 * Вердикт all-or-nothing: на реванше ([AnswerPhase.REVEALED]) раскрыты все примеры, верное поле
 * зелёное, неверное красное. Правильный ответ отдельной строкой НЕ печатаем (предложения длинные) —
 * у ошибочного примера появляется глазок, который подменяет содержимое поля эталоном и обратно.
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
    val revealed = phase == AnswerPhase.REVEALED
    // Акцент задания: пока отвечают — синий, когда всё верно — зелёный (точки и рамка пункта).
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
            exercise.items.forEachIndexed { index, item ->
                val input = inputs.getOrElse(index) { "" }
                TransformItemRow(
                    item = item,
                    value = input,
                    focused = revealed || index == focusedIndex,
                    visual = visualFor(revealed, item, input),
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

/** Результат примера для окраски поля: до реванша — нейтрально, на реванше — по совпадению ответа. */
private fun visualFor(revealed: Boolean, item: TransformItem, input: String): InputFieldVisual = when {
    !revealed -> InputFieldVisual.NEUTRAL
    AnswerNormalizer.matches(item.transformed, input) -> InputFieldVisual.CORRECT
    else -> InputFieldVisual.WRONG
}

/**
 * Один пример: свёрнут — строка-превью «исходное → вписанное» (общий [ExerciseAccordionItem]),
 * раскрыт — исходное предложение, под ним стрелка, поле преобразованного и слот глазка
 * ([ExercisePeekButton]) — слот занят всегда, кнопка в нём появляется у ошибочного примера.
 */
@Composable
private fun TransformItemRow(
    item: TransformItem,
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
        preview = previewTransform(item.original, value),
        previewMaxLines = PREVIEW_LINES,
        onFocus = onFocus,
    ) { fieldFocus ->
        // Ошибочный пример на реванше: глазок подменяет в поле ответ пользователя правильным и обратно.
        var peekCorrect by remember { mutableStateOf(false) }
        val wrong = visual == InputFieldVisual.WRONG

        // Исходное предложение — контентный EN.
        ExerciseContentText(text = item.original, fontSize = 18.sp)
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
                value = if (wrong && peekCorrect) item.transformed else value,
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
            // Слот глазка занят всегда (кнопка — только у ошибочного примера): ширина поля одинакова
            // до и после проверки, поэтому вердикт не переверстывает пример.
            ExercisePeekButton(
                visible = wrong,
                peeking = peekCorrect,
                onToggle = { peekCorrect = !peekCorrect },
            )
        }
    }
}

/**
 * Текст превью: «исходное → вписанное жирным». Пока поле пустое, за стрелкой стоит пропуск — он
 * рисуется линией ([ExerciseContentText]), как место под ответ в остальных заданиях. Стрелка `→`
 * превращается в ту же иконку, что и в раскрытом примере.
 */
private fun previewTransform(original: String, value: String): String {
    val typed = value.trim()
    return if (typed.isEmpty()) "$original → ___" else "$original → **$typed**"
}
