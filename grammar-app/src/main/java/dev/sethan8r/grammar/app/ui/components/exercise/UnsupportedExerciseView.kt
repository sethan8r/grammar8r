package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame

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
 * Аварийная плашка для [Exercise.Unsupported]: задание есть в индексе карточки, но его строки нет в
 * таблице своего типа (осиротевший индекс — рассинхрон content.db / баг конвейера). Показываем вместо
 * краша: тип + ID для диагностики и просьбу прислать скрин в поддержку. Отвечать нечего — «Далее»
 * сразу активна, сегмент в счёт «верно X из N» не идёт. В корректных данных пользователь её не видит.
 */
@Composable
fun UnsupportedExerciseView(exercise: Exercise.Unsupported, modifier: Modifier = Modifier) {
    PlaceholderBody(
        modifier = modifier,
        message = stringResource(R.string.exercise_load_failed),
        sub = stringResource(R.string.exercise_load_failed_ref, exercise.type.name, exercise.id),
        hint = stringResource(R.string.exercise_load_failed_support),
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

/**
 * Тело плашки-заглушки: основной текст [message], опциональная техническая подпись [sub]
 * (тип · ID) и опциональная просьба-подсказка [hint] (только у аварийной плашки — у AI-заглушки null).
 */
@Composable
private fun PlaceholderBody(
    message: String,
    sub: String?,
    modifier: Modifier = Modifier,
    hint: String? = null,
) {
    // shakeKey/pulseKey = 0 — плашки не трясутся и не пульсируют (ответа на них не бывает).
    ExerciseFrame(shakeKey = 0, pulseKey = 0, modifier = modifier) {
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
        if (hint != null) {
            Text(
                text = hint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.spaceMedium, start = Dimens.cardPadding, end = Dimens.cardPadding),
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}