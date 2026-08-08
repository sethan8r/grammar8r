package dev.sethan8r.grammar.app.ui.components.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/**
 * Шапка вкладки «Учить»: заголовок с лупой, а в режиме поиска — стрелка «назад» и поле ввода на
 * его месте. Состояния сменяются чистым кросс-фейдом: обе строки одной высоты и на всю ширину,
 * поэтому переход ничего не пересчитывает и текст с иконками никуда не съезжает. Сдвигается в
 * поиске только тело списка — переход читается как замена содержимого, а не как прилёт шапки.
 *
 * Режим поиска — не отдельный роут, а состояние экрана (CLAUDE.md), поэтому системный «Назад»
 * здесь не перехватывается: из поиска выводит стрелка слева от строки, а крестик внутри строки
 * сначала стирает запрос.
 *
 * [requestFocus] — одноразовый сигнал «поиск только что открыли». Клавиатура поднимается только
 * по нему: иначе она всплывала бы каждый раз, когда вкладка возвращается в композицию, — например
 * во время предпросмотра жеста «назад» на экране темы.
 */
@Composable
fun TheorySearchBar(
    isOpen: Boolean,
    query: String,
    requestFocus: Boolean,
    onQueryChange: (String) -> Unit,
    onOpen: () -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
    onFocusConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = isOpen,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = {
            fadeIn(tween(Durations.searchBarSwapMs))
                .togetherWith(fadeOut(tween(Durations.searchBarSwapMs / 2)))
                // Оба состояния одного размера — анимировать размер нечего, и попытка это делать
                // только вернула бы скачок соседних элементов.
                .using(sizeTransform = null)
        },
        label = "theory_search_bar",
    ) { open ->
        if (open) {
            SearchRow(
                query = query,
                requestFocus = requestFocus,
                onQueryChange = onQueryChange,
                onClear = onClear,
                onClose = onClose,
                onFocusConsumed = onFocusConsumed,
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
        // Шапка сдвинута влево под зону нажатия стрелки «назад»; заголовок добирает остаток до
        // общего отступа экрана, чтобы стоять по одной линии с карточками тем.
        Text(
            text = stringResource(R.string.theory_title),
            modifier = Modifier.padding(start = Dimens.spaceSmall),
            color = Accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        BarIconButton(
            icon = Icons.Filled.Search,
            contentDescription = stringResource(R.string.theory_search_open),
            onClick = onOpen,
        )
    }
}

/**
 * Открытое состояние шапки. Стрелка живёт СНАРУЖИ строки ввода — как на любом подэкране
 * приложения, той же иконкой и того же размера, что в `BackTopBar` (Правило №0: вид «назад»
 * один на всё).
 */
@Composable
private fun SearchRow(
    query: String,
    requestFocus: Boolean,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
    onFocusConsumed: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = stringResource(R.string.theory_search_close),
                tint = TextPrimary,
            )
        }
        SearchField(
            query = query,
            requestFocus = requestFocus,
            onQueryChange = onQueryChange,
            onClear = onClear,
            onClose = onClose,
            onFocusConsumed = onFocusConsumed,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    requestFocus: Boolean,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
    onFocusConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    // Поле хранит и текст, и позицию курсора: вернувшись в поиск из микротемы, пользователь должен
    // получить каретку в конце запроса, а не перед ним.
    var fieldValue by remember { mutableStateOf(TextFieldValue(query, TextRange(query.length))) }
    if (fieldValue.text != query) {
        fieldValue = TextFieldValue(query, TextRange(query.length))
    }

    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
            onFocusConsumed()
        }
    }

    Row(
        modifier = modifier
            .height(Dimens.searchFieldHeight)
            .clip(RoundedCornerShape(Dimens.cornerButton))
            .background(Elevated)
            // Справа отступа нет намеренно: крестик — тот же [BarIconButton] в конце строки на всю
            // ширину, что и лупа в закрытом состоянии, поэтому они встают в одну точку. Любой
            // отступ здесь сдвинул бы крестик относительно лупы, и переход стал бы заметен.
            .padding(start = Dimens.spaceMedium),
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
                value = fieldValue,
                onValueChange = {
                    fieldValue = it
                    onQueryChange(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(color = TextPrimary, fontSize = 16.sp),
                cursorBrush = SolidColor(Accent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
        }
        // Крестик сначала чистит введённое и оставляет пользователя в поиске; на пустом поле
        // второе нажатие закрывает режим — привычный порядок «стереть, потом выйти».
        BarIconButton(
            icon = Icons.Filled.Close,
            contentDescription = stringResource(
                if (query.isEmpty()) R.string.theory_search_close else R.string.theory_search_clear
            ),
            onClick = { if (query.isEmpty()) onClose() else onClear() },
        )
    }
}

/** Иконка-кнопка шапки: одна зона нажатия и один размер глифа на все состояния. */
@Composable
private fun BarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(Dimens.iconButtonSize)) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(Dimens.iconButtonGlyph),
            tint = Accent,
        )
    }
}
