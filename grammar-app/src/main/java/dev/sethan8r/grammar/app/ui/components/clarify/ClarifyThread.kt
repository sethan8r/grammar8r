package dev.sethan8r.grammar.app.ui.components.clarify

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.components.text.MarkdownText
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.InlineCode
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

/** Реплики треда уточнения: вопрос пользователя, ответ ИИ и ожидание ответа. */

private val userBubbleShape = RoundedCornerShape(
    topStart = Dimens.cornerCard,
    topEnd = Dimens.cornerCard,
    bottomStart = Dimens.cornerCard,
    bottomEnd = Dimens.cornerSmall,
)

private val aiBubbleShape = RoundedCornerShape(
    topStart = Dimens.cornerSmall,
    topEnd = Dimens.cornerCard,
    bottomStart = Dimens.cornerCard,
    bottomEnd = Dimens.cornerCard,
)

/**
 * Реплика пользователя — залитый акцентом пузырь справа с аватаром у нижнего угла (зеркально
 * реплике ИИ, где аватар стоит у верхнего). Слот [content] позволяет положить внутрь не только
 * текст вопроса, но и выбор из готовых вопросов карточки.
 */
@Composable
fun ClarifyUserMessage(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        // Пустое место слева — гарантированный зазор до края: пузырь тянется по тексту, но
        // «weight(fill = false)» не даёт ему занять всю ширину.
        Spacer(Modifier.width(Dimens.threadBubbleGutter))
        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(userBubbleShape)
                .background(Accent)
                .padding(Dimens.spaceMedium),
        ) {
            content()
        }
        Spacer(Modifier.width(Dimens.spaceSmall))
        UserAvatar()
    }
}

/** Заданный вопрос пользователя. */
@Composable
fun ClarifyQuestionBubble(question: String, modifier: Modifier = Modifier) {
    ClarifyUserMessage(modifier = modifier) {
        MarkdownText(
            text = question,
            modifier = Modifier.padding(horizontal = Dimens.spaceTiny),
            color = Background,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        )
    }
}

/** Ответ ИИ — фрейм слева с аватаром; появляется плавно, а не рывком. */
@Composable
fun ClarifyAnswerBubble(answer: String, modifier: Modifier = Modifier) {
    // Показываем анимацию один раз — на приход ответа. Флаг переживает выброс элемента из
    // композиции (список хранит состояние по ключу реплики), иначе ответ, уехавший за экран,
    // при возврате проигрывал бы появление заново.
    var alreadyShown by rememberSaveable { mutableStateOf(false) }
    // Стартуем со скрытого состояния и сразу переводим в видимое — иначе анимация входа не идёт.
    val visibility = remember { MutableTransitionState(alreadyShown).apply { targetState = true } }
    LaunchedEffect(Unit) { alreadyShown = true }
    AnimatedVisibility(
        visibleState = visibility,
        modifier = modifier,
        enter = fadeIn(tween(Durations.clarifyAnswerRevealMs)) +
            slideInVertically(tween(Durations.clarifyAnswerRevealMs)) { it / 6 },
    ) {
        AiRow {
            MarkdownText(
                text = answer,
                modifier = Modifier
                    .clip(aiBubbleShape)
                    .background(CardBackground)
                    .padding(horizontal = Dimens.cardPadding, vertical = Dimens.spaceMedium),
                color = TextPrimary,
                fontSize = 16.sp,
                lineHeight = 23.sp,
            )
        }
    }
}

/** Ожидание ответа: три точки, разгорающиеся по очереди. */
@Composable
fun ClarifyTypingBubble(modifier: Modifier = Modifier) {
    AiRow(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(aiBubbleShape)
                .background(CardBackground)
                .padding(horizontal = Dimens.cardPadding, vertical = Dimens.spaceLarge),
            horizontalArrangement = Arrangement.spacedBy(Dimens.typingDotGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TypingDots()
        }
    }
}

/** Запрос не прошёл — короткая строка вместо ответа, вопрос можно повторить. */
@Composable
fun ClarifyFailedBubble(message: String, modifier: Modifier = Modifier) {
    AiRow(modifier = modifier) {
        Text(
            text = message,
            modifier = Modifier
                .clip(aiBubbleShape)
                .background(CardBackground)
                .padding(horizontal = Dimens.cardPadding, vertical = Dimens.spaceMedium),
            color = IncorrectRed,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        )
    }
}

/** Общая раскладка реплики ИИ: аватар слева, тело — не шире доли ширины экрана. */
@Composable
private fun AiRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        AiAvatar()
        Spacer(Modifier.width(Dimens.spaceSmall))
        Box(modifier = Modifier.weight(1f, fill = false)) { content() }
        Spacer(Modifier.width(Dimens.threadBubbleGutter))
    }
}

/** TODO: заменить кружок на логотип приложения, когда он будет нарисован. */
@Composable
private fun AiAvatar() {
    val description = stringResource(R.string.clarify_ai_avatar_cd)
    Box(
        modifier = Modifier
            .padding(top = Dimens.spaceTiny)
            .size(Dimens.avatarSize)
            .clip(CircleShape)
            .background(Accent)
            .semantics { contentDescription = description },
    )
}

/** TODO: заменить заглушку на аватар пользователя, когда появится загрузка своего изображения. */
@Composable
private fun UserAvatar() {
    Box(
        modifier = Modifier
            .padding(bottom = Dimens.spaceTiny)
            .size(Dimens.avatarSize)
            .clip(CircleShape)
            .background(Elevated),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = stringResource(R.string.clarify_user_avatar_cd),
            tint = TextSecondary,
            modifier = Modifier.size(Dimens.avatarGlyph),
        )
    }
}

/**
 * Точки «ИИ печатает». Готового компонента под такую индикацию нет, но и ручная машина состояний
 * не нужна: [rememberInfiniteTransition] — официальный API для бесконечных циклов, сдвиг фазы
 * между точками задаётся [StartOffset].
 */
@Composable
private fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "clarifyTyping")
    repeat(TYPING_DOT_COUNT) { index ->
        val alpha by transition.animateFloat(
            initialValue = Alphas.typingDotMin,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(Durations.typingDotCycleMs, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(index * Durations.typingDotStaggerMs),
            ),
            label = "clarifyTypingDot$index",
        )
        Box(
            modifier = Modifier
                .size(Dimens.typingDotSize)
                .clip(CircleShape)
                .background(InlineCode.copy(alpha = alpha)),
        )
    }
}

private const val TYPING_DOT_COUNT = 3
