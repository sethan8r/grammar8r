package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.theory.TheoryListItem
import dev.sethan8r.grammar.app.domain.model.theory.TopicSummary
import dev.sethan8r.grammar.app.ui.components.CenteredHint
import dev.sethan8r.grammar.app.ui.components.DualTitle
import dev.sethan8r.grammar.app.ui.components.InfoButton
import dev.sethan8r.grammar.app.ui.components.feedback.LocalTabSnackbarController
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.floatingBarBottomInset
import dev.sethan8r.grammar.app.ui.util.statusBarTopInset

/**
 * Вкладка «Учить» — список разделов и тем теории с прогрессом по микротемам. Раздел сворачивается;
 * описания скрыты под кнопкой «i» ([InfoButton]) — по тапу всплывают нижним снекбаром. Только UI:
 * данные из [TheoryViewModel].
 */
@Composable
fun TheoryScreen(
    onTopicClick: (Int) -> Unit,
    viewModel: TheoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Снекбар вкладок общий (висит над капсулой навигации в MainScreen) — берём из CompositionLocal.
    val snackbar = LocalTabSnackbarController.current

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> CenteredHint(stringResource(R.string.theory_loading), Modifier.fillMaxSize())
            uiState.items.isEmpty() -> CenteredHint(stringResource(R.string.theory_empty), Modifier.fillMaxSize())
            else -> TheoryList(
                items = uiState.items,
                onTopicClick = onTopicClick,
                onShowInfo = { snackbar?.show(it, Durations.infoSnackbarMs) },
            )
        }
    }
}

@Composable
private fun TheoryList(
    items: List<TheoryListItem>,
    onTopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenPadding),
        // Верх: под строку состояния (первый элемент на месте, при скролле проезжает под неё).
        // Низ: клиренс под плавающей капсулой навигации (она парит поверх, места не резервирует).
        contentPadding = PaddingValues(
            top = statusBarTopInset(),
            bottom = floatingBarBottomInset(),
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
    ) {
        item {
            Text(
                text = stringResource(R.string.theory_title),
                modifier = Modifier.padding(vertical = Dimens.spaceLarge),
                color = Accent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        items(items = items, key = { it.itemKey() }) { item ->
            when (item) {
                is TheoryListItem.TopicItem -> TopicBody(
                    topic = item.topic,
                    onTopicClick = onTopicClick,
                    onShowInfo = onShowInfo,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.cornerCard))
                        .background(CardBackground),
                )
                is TheoryListItem.SectionItem -> SectionGroup(item, onTopicClick, onShowInfo)
            }
        }
    }
}

private fun TheoryListItem.itemKey(): String = when (this) {
    is TheoryListItem.TopicItem -> "topic_${topic.id}"
    is TheoryListItem.SectionItem -> "section_$id"
}

/**
 * Раздел в едином фрейме (как раскрытие категории в словаре Words8r): кликабельная шапка со
 * стрелкой ▾/▸ и кнопкой «i», а раскрытые темы лежат внутри того же фрейма, разделённые линиями.
 */
@Composable
private fun SectionGroup(
    section: TheoryListItem.SectionItem,
    onTopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
) {
    var expanded by rememberSaveable(section.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.cornerCard))
            .background(CardBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(Dimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val rotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                label = "section_arrow",
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.rotate(rotation),
            )
            DualTitle(
                title = section.title,
                modifier = Modifier.weight(1f).padding(start = Dimens.spaceSmall),
                primarySize = 18.sp,
            )
            section.description?.let { description -> InfoButton(onClick = { onShowInfo(description) }) }
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                section.topics.forEach { topic ->
                    HorizontalDivider(color = Inactive)
                    TopicBody(topic, onTopicClick, onShowInfo)
                }
            }
        }
    }
}

/**
 * Содержимое темы (название + прогресс + «i»), без собственного фона. Отдельная тема оборачивается
 * фреймом снаружи; внутри раздела рисуется строкой внутри его фрейма.
 */
@Composable
private fun TopicBody(
    topic: TopicSummary,
    onTopicClick: (Int) -> Unit,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTopicClick(topic.id) }
            .padding(Dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
    ) {
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