package dev.sethan8r.grammar.app.ui.components.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.sethan8r.grammar.app.ui.components.HaloBox
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.util.bottomScrim
import dev.sethan8r.grammar.app.ui.util.topScrim

/** Доля высоты полоски, на которой затемнение выходит на полную силу — как у скрима над навигацией. */
private const val FadeSaturateAt = 0.8f

/**
 * Модальное окно со справочным содержимым произвольной длины: заголовок, прокручиваемое тело и
 * парящая над ним капсула закрытия. Тело — слот [content]: вызывающий кладёт туда готовый контент
 * из БД. Сейчас единственный вызов — «Краткое правило» по кнопке «?» в сессии упражнений
 * (`ExerciseSessionScreen`); описания тем показываются иначе, нижним снекбаром по кнопке «i».
 *
 * Заголовок — закреплённая шапка поверх тела, как на экранах приложения: тело отступает вниз на её
 * известную высоту ([Dimens.dialogHeaderHeight]), поэтому в покое текст стоит под заголовком, а при
 * прокрутке проезжает под ним и гасится тем же затемнением ([topScrim]).
 *
 * Тело скроллится, поэтому длинный текст виден целиком, а капсула всегда на месте: она прижата к
 * правому нижнему углу поверх текста, текст доходит до самого края окна и проезжает под ней, но в
 * конце прокрутки упирается в пустое поле — последняя строка остаётся НАД капсулой и ничем не
 * перекрыта. Пока прокрутка не дошла до конца, нижний край гасится узкой полоской затемнения (как у
 * скроллящихся экранов приложения): видно, что текст продолжается, а не обрывается.
 *
 * Размер окна задаём сами: системная ширина диалога съедает треть экрана и рвёт длинные английские
 * вставки, а поля от краёв держат самый длинный текст в стороне от статус-бара и навигационной полосы.
 */
@Composable
fun InfoDialog(
    title: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    val contentPadding = Dimens.spaceXLarge
    // Место под капсулу в конце прокрутки: сама капсула с её отступом от низа плюс зазор до текста.
    val footerSpace = Dimens.dialogCloseHeight + contentPadding + Dimens.spaceMedium

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.dialogMargin, vertical = Dimens.dialogMarginVertical),
            color = CardBackground,
            shape = RoundedCornerShape(Dimens.cornerCard),
        ) {
            // Края не заполняем: текст доходит до самого верха и низа окна — вверху проезжает под
            // шапкой, внизу уходит под капсулу закрытия.
            Box {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(horizontal = contentPadding),
                ) {
                    // Тело начинается ПОД шапкой — тем же приёмом, каким списки экранов отступают
                    // под закреплённую шапку: высота известна заранее, поэтому текст стоит на месте
                    // с первого кадра.
                    Spacer(modifier = Modifier.height(Dimens.dialogHeaderHeight))
                    content()
                    Spacer(modifier = Modifier.height(footerSpace))
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .height(Dimens.dialogHeaderHeight)
                        .topScrim(color = CardBackground, alpha = Alphas.headerScrim)
                        .padding(horizontal = contentPadding, vertical = Dimens.spaceLarge),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Text(
                        text = title,
                        color = Accent,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                ScrollFade(
                    visible = scrollState.canScrollForward,
                    height = Dimens.dialogFadeHeight,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )

                HaloBox(
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = contentPadding, bottom = contentPadding),
                ) {
                    Row(
                        modifier = Modifier
                            .height(Dimens.dialogCloseHeight)
                            .clip(CircleShape)
                            .background(Elevated)
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = Dimens.spaceXLarge),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = confirmLabel,
                            color = Accent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Полоска затемнения у края прокручиваемого тела: гасит уезжающий текст и показывает, что он
 * продолжается. У нижнего края ([atTop] = false) или у верхнего, под заголовком. Появляется и
 * исчезает плавно, вместе с возможностью прокрутки в эту сторону.
 *
 * Отдельной функцией, а не блоком внутри диалога: в теле `Box` виден ещё и `ColumnScope` внешнего
 * столбца, и вызов `AnimatedVisibility` там разрешается в его перегрузку.
 */
@Composable
private fun ScrollFade(
    visible: Boolean,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .bottomScrim(
                    color = CardBackground,
                    alpha = Alphas.statusScrim,
                    saturateAt = FadeSaturateAt,
                ),
        )
    }
}
