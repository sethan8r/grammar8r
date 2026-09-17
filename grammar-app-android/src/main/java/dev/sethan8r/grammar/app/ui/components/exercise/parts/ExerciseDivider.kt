package dev.sethan8r.grammar.app.ui.components.exercise.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive

/**
 * Разделитель внутри [ExerciseFrame] (в стиле плашек «Кстати»): толстая скруглённая полоса с
 * отступом от краёв фрейма. Единый разделитель для всех заданий (Правило №0): отделяет шапку-условие
 * от вариантов и блок объяснения от ответа. Рендерится прямым потомком `ColumnScope` фрейма — у него
 * нет горизонтального паддинга, поэтому полоса сама задаёт свой инсет [Dimens.calloutDividerInset].
 */
@Composable
fun ExerciseDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.cardPadding, horizontal = Dimens.calloutDividerInset)
            .height(Dimens.calloutDividerThickness)
            .clip(RoundedCornerShape(percent = 50))
            .background(Inactive),
    )
}