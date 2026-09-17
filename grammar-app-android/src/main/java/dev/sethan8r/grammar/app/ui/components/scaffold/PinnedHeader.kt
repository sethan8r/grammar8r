package dev.sethan8r.grammar.app.ui.components.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sethan8r.grammar.app.ui.util.statusBarTopInset
import dev.sethan8r.grammar.app.ui.util.topScrim

/**
 * Закреплённая шапка, под которую проезжает контент экрана. Кладётся поверх контента (последним
 * ребёнком `Box`), начинается от самого верха экрана и затемняет проезжающее под ней — вместе со
 * строкой состояния. Контент экрана отступает под шапку сам, чтобы в покое не прятаться под ней:
 * при фиксированных строках — суммой токенов (строка состояния + высоты строк), при строке
 * переменной высоты (перенос текста) — по фактической высоте шапки.
 */
@Composable
fun PinnedHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .topScrim()
            .padding(top = statusBarTopInset()),
        content = content,
    )
}