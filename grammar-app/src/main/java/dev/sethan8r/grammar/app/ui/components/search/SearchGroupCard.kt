package dev.sethan8r.grammar.app.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.sethan8r.grammar.app.domain.model.theory.SearchGroup
import dev.sethan8r.grammar.app.ui.components.theory.MicrotopicRow
import dev.sethan8r.grammar.app.ui.components.theory.MicrotopicStatusStyle
import dev.sethan8r.grammar.app.ui.components.theory.TopicCardBody
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive

/**
 * Группа выдачи: карточка темы, под ней — панель совпавших микротем. Панель залита цветом фона
 * приложения, поэтому читается «утопленной» внутрь карточки.
 *
 * Тема, совпавшая сама по себе, приходит без микротем — тогда это просто короткая карточка,
 * никаких заглушек ради заполнения.
 */
@Composable
fun SearchGroupCard(
    group: SearchGroup,
    onTopicClick: (Int) -> Unit,
    onMicrotopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        TopicCardBody(
            topic = group.topic,
            onTopicClick = onTopicClick,
            onShowInfo = onShowInfo,
            sectionTitle = group.sectionTitle,
        )
        if (group.microtopics.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(
                        start = Dimens.spaceSmall,
                        end = Dimens.spaceSmall,
                        bottom = Dimens.spaceSmall,
                    )
                    .clip(RoundedCornerShape(Dimens.cornerCard))
                    .background(Background),
            ) {
                group.microtopics.forEachIndexed { index, microtopic ->
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = Dimens.cardPadding),
                            color = Inactive,
                        )
                    }
                    MicrotopicRow(
                        microtopic = microtopic,
                        onMicrotopicClick = onMicrotopicClick,
                        statusStyle = MicrotopicStatusStyle.DOT,
                    )
                }
            }
        }
    }
}
