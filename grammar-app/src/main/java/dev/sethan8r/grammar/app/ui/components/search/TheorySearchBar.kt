package dev.sethan8r.grammar.app.ui.components.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Шапка вкладки «Учить»: заголовок с лупой, а в режиме поиска — поле ввода на его месте.
 * Оба состояния живут в одном слоте списка и сменяют друг друга анимацией, поэтому строка
 * «перекрывает» заголовок без наложения слоёв и липкой шапки.
 *
 * Режим поиска — не отдельный роут, а состояние экрана (CLAUDE.md), поэтому «Назад» здесь не
 * перехватывается: выход из поиска — крестиком.
 */
@Composable
fun TheorySearchBar(
    isOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(isOpen) {
        if (isOpen) {
            focusRequester.requestFocus()
            keyboard?.show()
        } else {
            keyboard?.hide()
        }
    }

    AnimatedContent(
        targetState = isOpen,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = {
            val duration = Durations.searchBarSwapMs
            (slideInVertically(tween(duration)) { -it / 4 } + fadeIn(tween(duration)))
                .togetherWith(fadeOut(tween(duration)))
        },
        label = "theory_search_bar",
    ) { open ->
        if (open) {
            SearchField(
                query = query,
                onQueryChange = onQueryChange,
                onClose = onClose,
                focusRequester = focusRequester,
            )
        } else {
            TitleRow(onOpen = onOpen)
        }
    }
}

@Composable
private fun TitleRow(onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.searchFieldHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.theory_title),
            color = Accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        SearchModeButton(isOpen = false, onClick = onOpen)
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    focusRequester: FocusRequester,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.searchFieldHeight)
            .clip(RoundedCornerShape(Dimens.cornerButton))
            .background(Elevated)
            .padding(start = Dimens.spaceMedium, end = Dimens.spaceTiny),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier.size(Dimens.iconButtonGlyph),
            tint = TextSecondary,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimens.spaceSmall),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.theory_search_placeholder),
                    color = TextSecondary,
                    fontSize = 16.sp,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(color = TextPrimary, fontSize = 16.sp),
                cursorBrush = SolidColor(Accent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
        }
        SearchModeButton(isOpen = true, onClick = onClose)
    }
}

/** Лупа и крестик — одна кнопка: меняется только иконка, поэтому позиция не «прыгает». */
@Composable
private fun SearchModeButton(isOpen: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(Dimens.iconButtonSize)) {
        AnimatedContent(
            targetState = isOpen,
            transitionSpec = {
                val duration = Durations.searchBarSwapMs
                (fadeIn(tween(duration)) + scaleIn(tween(duration)))
                    .togetherWith(fadeOut(tween(duration)) + scaleOut(tween(duration)))
            },
            label = "theory_search_icon",
        ) { open ->
            Icon(
                imageVector = if (open) Icons.Filled.Close else Icons.Filled.Search,
                contentDescription = stringResource(
                    if (open) R.string.theory_search_close else R.string.theory_search_open
                ),
                modifier = Modifier.size(Dimens.iconButtonGlyph),
                tint = Accent,
            )
        }
    }
}
