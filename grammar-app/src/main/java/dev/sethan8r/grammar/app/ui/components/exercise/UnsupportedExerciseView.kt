package dev.sethan8r.grammar.app.ui.components.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Заглушка типа упражнения, который движок ещё не реализовал (F2–F4). Отвечать нечего — «Далее»
 * (в скаффолде) сразу активна; сегмент проходится, в счёт «верно X из N» не идёт. Уберётся сама,
 * когда все 14 типов получат рендереры.
 */
@Composable
fun UnsupportedExerciseView(exercise: Exercise.Unsupported, modifier: Modifier = Modifier) {
    PlaceholderBody(
        modifier = modifier,
        message = stringResource(R.string.exercise_type_in_development),
        sub = exercise.type.name,
    )
}

/** Заглушка умного (AI) задания карточки — Фаза 3. */
@Composable
fun AiPlaceholderView(modifier: Modifier = Modifier) {
    PlaceholderBody(
        modifier = modifier,
        message = stringResource(R.string.exercise_ai_placeholder),
        sub = null,
    )
}

@Composable
private fun PlaceholderBody(message: String, sub: String?, modifier: Modifier = Modifier) {
    // shakeKey = 0 — плашки не трясутся (неверного ответа на них не бывает).
    ExerciseFrame(shakeKey = 0, modifier = modifier) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.cardPadding),
            color = TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        if (sub != null) {
            Text(
                text = sub,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.spaceSmall, start = Dimens.cardPadding, end = Dimens.cardPadding),
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}