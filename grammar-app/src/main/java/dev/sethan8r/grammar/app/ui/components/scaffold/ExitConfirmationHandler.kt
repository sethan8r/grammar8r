package dev.sethan8r.grammar.app.ui.components.scaffold

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import kotlin.coroutines.cancellation.CancellationException

/**
 * Перехват «Назад» ради подтверждения выхода — ЕДИНСТВЕННОЕ разрешённое исключение из запрета
 * `BackHandler` (CLAUDE → «Навигация»). Реализован ОДИН раз и переиспользуется везде, где есть
 * несохранённый прогресс (начатая сессия упражнений, случайная практика и т.д.).
 *
 * Использует официальный [PredictiveBackHandler] (дружит с жестом-превью «Назад»). Диалог показывается
 * и по жесту/кнопке системного «Назад», и по [showDialog] (кнопка «Назад» в шапке экрана): экран
 * владеет флагом, компонент рисует диалог и сам поднимает флаг при back-жесте. Заголовок по центру,
 * кнопки враспор. Подтверждение → [onConfirmExit] (обычный `popBackStack`); отмена закрывает диалог.
 */
@Composable
fun ExitConfirmationHandler(
    enabled: Boolean,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    onConfirmExit: () -> Unit,
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
) {
    PredictiveBackHandler(enabled = enabled && !showDialog) { progress ->
        try {
            progress.collect { /* превью жеста — ждём завершения */ }
            onShowDialogChange(true)
        } catch (_: CancellationException) {
            // жест отменён — выхода не происходит
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { onShowDialogChange(false) }) {
            Surface(color = CardBackground, shape = RoundedCornerShape(Dimens.cornerCard)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.spaceXLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.spaceMedium),
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.spaceLarge),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        TextButton(onClick = { onShowDialogChange(false) }) { Text(dismissLabel) }
                        TextButton(onClick = {
                            onShowDialogChange(false)
                            onConfirmExit()
                        }) { Text(confirmLabel) }
                    }
                }
            }
        }
    }
}