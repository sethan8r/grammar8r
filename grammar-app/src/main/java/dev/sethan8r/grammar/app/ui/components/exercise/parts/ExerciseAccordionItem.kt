package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/** Морф пункта «свёрнут ↔ в фокусе»: размер плашки и её цвета, мс. */
private const val ITEM_MORPH_MS = 240

/**
 * Пункт задания с несколькими полями ввода в раскладке «аккордеон-фокус»: раскрыт ровно один пункт,
 * остальные свёрнуты в строку «точка-индикатор + приглушённое превью». Тап по свёрнутому пункту
 * отдаёт фокус ему ([onFocus]); раскрытый пункт на тап не реагирует — фокус нельзя «снять».
 *
 * Общий для всех многопунктовых заданий (Правило №0): плашка ([Elevated] + рамка цветом [accent]),
 * морф содержимого ([AnimatedContent]: перекрёстное затухание + плавная смена высоты) и перенос
 * системного фокуса живут здесь, а тело раскрытого пункта каждое задание рисует само — слотом
 * [content], которому передаётся [FocusRequester] его поля ввода.
 *
 * - [filled] — в поле уже что-то вписано (цвет точки-индикатора).
 * - [outlined] — рисовать рамку акцентом: признак «сюда печатать». Когда поля окрашены результатом
 *   (реванш), рамку гасим — иначе она спорит с зелёным/красным.
 * - [grabKeyboard] — пункт раскрывается, когда ввод уже идёт: поле сразу забирает фокус, чтобы
 *   клавиатура не мигнула вниз-вверх. Иначе фокус не запрашивается и клавиатура не всплывает сама.
 * - [preview] — строка-превью свёрнутого пункта (разметка [ExerciseContentText]: `**жирный**`,
 *   стрелки, `___` как линия-пропуск), [previewMaxLines] — сколько строк ей отведено.
 */
@Composable
fun ExerciseAccordionItem(
    focused: Boolean,
    filled: Boolean,
    accent: Color,
    outlined: Boolean,
    grabKeyboard: Boolean,
    preview: String,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier,
    previewMaxLines: Int = 1,
    content: @Composable ColumnScope.(fieldFocus: FocusRequester) -> Unit,
) {
    val fieldFocus = remember { FocusRequester() }
    val shape = RoundedCornerShape(Dimens.cornerButton)
    val fill by animateColorAsState(
        targetValue = if (focused) Elevated else Color.Transparent,
        animationSpec = tween(ITEM_MORPH_MS),
        label = "itemFill",
    )
    val borderColor by animateColorAsState(
        targetValue = if (focused && outlined) accent else Color.Transparent,
        animationSpec = tween(ITEM_MORPH_MS),
        label = "itemBorder",
    )
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(fill)
            .border(1.dp, borderColor, shape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !focused,
                onClick = onFocus,
            )
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
    ) {
        AnimatedContent(
            targetState = focused,
            transitionSpec = {
                ContentTransform(
                    // Новое содержимое всплывает после того, как ушло старое — без наложения текстов.
                    targetContentEnter = fadeIn(tween(ITEM_MORPH_MS, delayMillis = ITEM_MORPH_MS / 2)),
                    initialContentExit = fadeOut(tween(ITEM_MORPH_MS / 2)),
                    sizeTransform = SizeTransform(clip = false) { _, _ -> tween(ITEM_MORPH_MS) },
                )
            },
            label = "itemMorph",
        ) { isFocused ->
            if (isFocused) {
                // Ввод уже шёл — поле подхватывает фокус в кадре появления (клавиатура не мигает).
                if (grabKeyboard) {
                    LaunchedEffect(Unit) { fieldFocus.requestFocus() }
                }
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny)) {
                    content(fieldFocus)
                }
            } else {
                CollapsedPreview(
                    preview = preview,
                    filled = filled,
                    accent = accent,
                    maxLines = previewMaxLines,
                )
            }
        }
    }
}

/** Свёрнутый пункт: точка-индикатор (заполнен — [accent], пуст — [Inactive]) и превью с эллипсисом. */
@Composable
private fun CollapsedPreview(preview: String, filled: Boolean, accent: Color, maxLines: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
    ) {
        Box(
            Modifier
                .size(Dimens.exerciseItemDot)
                .background(if (filled) accent else Inactive, CircleShape),
        )
        ExerciseContentText(
            text = preview,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}
