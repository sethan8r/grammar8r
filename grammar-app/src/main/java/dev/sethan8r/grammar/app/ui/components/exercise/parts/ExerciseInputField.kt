package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextPrimary

/** Состояние рамки поля на реванше: нейтральное / верно (зелёная) / неверно (красная). */
enum class InputFieldVisual { NEUTRAL, CORRECT, WRONG }

/**
 * Единое поле ввода ответа в хардкод-заданиях (Правило №0): рамка-фрейм, ввод по центру, курсор-акцент.
 * Используется инлайн-пропуском TEXT_INPUT, ячейками TABLE_FILL и полями TRANSFORMATION — размер задаёт
 * вызывающий через [modifier] (em-плейсхолдер у инлайна / фикс. бокс у таблицы).
 *
 * Автоподсказки/Т9 выключены: в хардкод-задании подсказка клавиатуры = подсказка ответа (чит). Надёжно
 * глушит подсказки `KeyboardType.Password` (Gboard игнорит один `autoCorrectEnabled`) — как в Words8r;
 * текст при этом остаётся видимым, т.к. маскирует не тип клавиатуры, а `VisualTransformation`, а у
 * [BasicTextField] она по умолчанию `None`. В AI-вводе (Фаза 3, отдельный компонент) Т9 оставляем.
 */
@Composable
fun ExerciseInputField(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visual: InputFieldVisual = InputFieldVisual.NEUTRAL,
) {
    val borderColor = when (visual) {
        InputFieldVisual.NEUTRAL -> Inactive
        InputFieldVisual.CORRECT -> CorrectGreen
        InputFieldVisual.WRONG -> IncorrectRed
    }
    // На реванше поле ЗАЛИВАЕТСЯ результатом (зелёное/красное с прозрачностью), не только обводка.
    val fillColor = when (visual) {
        InputFieldVisual.NEUTRAL -> Background
        InputFieldVisual.CORRECT -> CorrectGreen.copy(alpha = Alphas.answerFill)
        InputFieldVisual.WRONG -> IncorrectRed.copy(alpha = Alphas.answerFill)
    }
    // Каретка живёт внутри: наружу отдаём только текст, а позицию курсора держим сами — иначе поле,
    // пересозданное при возврате к пункту, ставит каретку в начало уже вписанного слова.
    var field by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    // Текст пришёл извне (сброс/восстановление ответа) — принимаем его с кареткой в конце.
    if (field.text != value) {
        field = TextFieldValue(value, TextRange(value.length))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.cornerSmall))
            .background(fillColor)
            .border(1.dp, borderColor, RoundedCornerShape(Dimens.cornerSmall)),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = field,
            onValueChange = {
                field = it
                onValueChange(it.text)
            },
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(color = TextPrimary, fontSize = 18.sp, textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
            ),
            cursorBrush = SolidColor(Accent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceTiny),
        )
    }
}