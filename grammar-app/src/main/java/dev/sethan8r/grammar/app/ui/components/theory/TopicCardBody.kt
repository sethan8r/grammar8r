package dev.sethan8r.grammar.app.ui.components.theory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.theory.TopicSummary
import dev.sethan8r.grammar.app.ui.components.DualTitle
import dev.sethan8r.grammar.app.ui.components.InfoButton
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Тело карточки темы: название, прогресс по микротемам и кнопка «i» с описанием.
 * Один компонент на все места, где тема показывается — вкладка «Учить», раздел и выдача поиска;
 * в выдаче сверху добавляется подпись раздела ([sectionTitle]).
 *
 * Фон и скругление задаёт вызывающий через [modifier] — снаружи тема живёт то отдельной карточкой,
 * то строкой внутри общего фрейма раздела.
 */
@Composable
fun TopicCardBody(
    topic: TopicSummary,
    onTopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTopicClick(topic.id) }
            .padding(Dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
    ) {
        sectionTitle?.let { section ->
            Text(text = section, color = TextSecondary, fontSize = 13.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            DualTitle(title = topic.title, modifier = Modifier.weight(1f), primarySize = 18.sp)
            topic.description?.let { description -> InfoButton(onClick = { onShowInfo(description) }) }
        }
        if (topic.totalMicrotopics > 0) {
            LinearProgressIndicator(
                progress = { topic.completedMicrotopics.toFloat() / topic.totalMicrotopics },
                modifier = Modifier.fillMaxWidth(),
                color = Accent,
                trackColor = Inactive,
            )
            Text(
                text = stringResource(
                    R.string.theory_progress_format,
                    topic.completedMicrotopics,
                    topic.totalMicrotopics,
                ),
                color = TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}
