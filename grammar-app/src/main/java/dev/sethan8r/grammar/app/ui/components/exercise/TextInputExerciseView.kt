package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseContentText
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseInputField
import dev.sethan8r.grammar.app.ui.components.exercise.parts.InputFieldVisual

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as colorLerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.app.domain.model.exercise.TextItem
import dev.sethan8r.grammar.app.domain.usecase.ExerciseEvaluator
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private const val BLANK_ID = "blank"
private const val BANK_DOT_ID = "bankDot"

/** Банк-подсказка: держится раскрытым, мс; длительность морфа, мс. */
private const val BANK_PEEK_MS = 5_000L
private const val BANK_ANIM_MS = 260

/** Фикс. высота footprint'а капсулы (место резервируется всегда — при раскрытии ничего не двигается). */
private val BANK_CAPSULE_H = 32.dp

/** Масштаб свёрнутой полоски относительно раскрытой капсулы (морф идёт от этих значений к 1f). */
private const val BANK_STRIP_SX = 0.30f
private const val BANK_STRIP_SY = 0.16f

/** Морф пункта «свёрнут ↔ в фокусе»: размер и цвета плашки, мс. */
private const val ITEM_MORPH_MS = 240

/**
 * Рендерер TEXT_INPUT во [ExerciseFrame] (трясётся на ошибке). Пропуск `___` — это инлайн-поле ввода
 * прямо в предложении (пользователь печатает в него, а не в отдельное поле), ширина — по длине
 * правильного ответа. Проверка ввода — case-insensitive с учётом сокращений
 * ([dev.sethan8r.grammar.app.domain.usecase.AnswerNormalizer]).
 *
 * Раскладка — аккордеон-фокус: пока идёт ответ, раскрыт ровно ОДИН пункт (предложение с полем +
 * контекст), остальные свёрнуты в строку «точка-индикатор + приглушённое превью». Тап по свёрнутой
 * строке переводит фокус на неё, прошлая сворачивается — так задание из 4–5 пунктов не растёт в
 * простыню. На реванше ([AnswerPhase.REVEALED]) аккордеон выключается: раскрыты все пункты, поле
 * каждого окрашено по своему результату (зелёная/красная рамка), под неверным — правильное слово
 * зелёным. Вердикт задания при этом остаётся all-or-nothing (его держит экран сессии).
 *
 * Режим «банк слов» (когда есть [Exercise.TextInput.wordBank]): сверху — шапка-задание +
 * [ExerciseDivider], а В САМОМ НИЗУ — [WordBankPeek]: голубая полоска, которая по тапу «разъезжается»
 * в серую капсулу со словами (масштаб от центра + цвет + альфа текста, footprint зарезервирован —
 * ничего не сдвигается) на [BANK_PEEK_MS] и сворачивается обратно. Пер-пунктовые глоссы (contextRu)
 * у таких заданий отсутствуют.
 */
@Composable
fun TextInputExerciseView(
    exercise: Exercise.TextInput,
    answer: ExerciseAnswer.TextAnswers?,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onChange: (itemIndex: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase.isEditable
    val inputs = answer?.inputs.orEmpty()
    val task = exercise.taskDescription
    val revealed = phase == AnswerPhase.REVEALED
    val solved = phase == AnswerPhase.CORRECT
    // Акцент задания: пока отвечают — синий, когда всё верно — зелёный (точки и рамка пункта).
    val accent = if (solved) CorrectGreen else Accent
    // Пункт «в фокусе». На реванше индекс не важен — там раскрыты все.
    var focusedIndex by rememberSaveable { mutableIntStateOf(0) }
    // Состояние клавиатуры при смене пункта не меняется: идёт ввод (IME на экране) — фокус переезжает
    // на поле нового пункта и клавиатура остаётся; клавиатуры нет — фокус не запрашиваем вообще.
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey, modifier = modifier) {
        if (!task.isNullOrBlank()) {
            ExerciseContentText(
                text = task,
                color = TextPrimary,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            )
            // Линия отделяет шапку-задание от предложений.
            ExerciseDivider()
        }

        Column(
            modifier = Modifier.padding(horizontal = Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        ) {
            exercise.items.forEachIndexed { index, item ->
                val input = inputs.getOrElse(index) { "" }
                TextInputItem(
                    item = item,
                    value = input,
                    focused = revealed || index == focusedIndex,
                    // Глоссы — только пока отвечают и только без банка (в банк-режиме подсказка общая, внизу).
                    showContext = editable && exercise.wordBank.isEmpty(),
                    visual = visualFor(revealed, item, input),
                    accent = accent,
                    editable = editable,
                    grabKeyboard = editable && index == focusedIndex && imeVisible,
                    onValueChange = { onChange(index, it) },
                    onFocus = { focusedIndex = index },
                )
            }
        }

        // Подсказка-пул под предложениями: полоска -> тап -> «разъезжается» в капсулу со словами.
        // Ответ уже принят (верно / реванш) — подсказывать нечего, капсулу убираем.
        if (exercise.wordBank.isNotEmpty() && editable) {
            WordBankPeek(
                words = exercise.wordBank,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.cardPadding)
                    .padding(top = Dimens.spaceLarge),
            )
        }

        // Объяснение на реванше — единый блок (линия + текст), общий с заданиями выбора варианта.
        ExerciseExplanation(phase = phase, text = exercise.explanation)
    }
}

/** Результат пункта для окраски поля: до реванша — нейтрально, на реванше — по совпадению ответа. */
private fun visualFor(revealed: Boolean, item: TextItem, input: String): InputFieldVisual = when {
    !revealed -> InputFieldVisual.NEUTRAL
    ExerciseEvaluator.matchesTextItem(item, input) -> InputFieldVisual.CORRECT
    else -> InputFieldVisual.WRONG
}

/**
 * Один пункт задания в двух состояниях: [focused] — предложение с инлайн-полем (+ контекст, + на
 * реванше правильное слово под неверным полем), иначе — свёрнутая строка-превью. Плашка вокруг
 * пункта проявляется вместе с фокусом ([Elevated] + рамка цветом [accent]), содержимое сменяется через
 * [AnimatedContent] (перекрёстное затухание + плавная смена высоты, `SizeTransform`) — поэтому
 * соседние пункты разъезжаются анимированно, а не прыжком. Тап работает только на свёрнутом
 * пункте: фокус нельзя «снять», в задании всегда раскрыт ровно один (или все — на реванше).
 *
 * [grabKeyboard] — пункт раскрывается, когда ввод уже идёт: поле сразу забирает системный фокус, чтобы
 * клавиатура не мигнула вниз-вверх. Без него фокус не запрашивается и клавиатура не всплывает сама.
 */
@Composable
private fun TextInputItem(
    item: TextItem,
    value: String,
    focused: Boolean,
    showContext: Boolean,
    visual: InputFieldVisual,
    accent: Color,
    editable: Boolean,
    grabKeyboard: Boolean,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
) {
    val fieldFocus = remember { FocusRequester() }
    val shape = RoundedCornerShape(Dimens.cornerButton)
    val fill by animateColorAsState(
        targetValue = if (focused) Elevated else Color.Transparent,
        animationSpec = tween(ITEM_MORPH_MS),
        label = "itemFill",
    )
    // Рамка-акцент — признак «сюда печатать». На реванше результат держит само поле, рамку гасим.
    val borderColor by animateColorAsState(
        targetValue = if (focused && visual == InputFieldVisual.NEUTRAL) accent else Color.Transparent,
        animationSpec = tween(ITEM_MORPH_MS),
        label = "itemBorder",
    )
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(fill)
            .border(1.dp, borderColor, shape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !focused,
                onClick = onFocus,
            )
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
    ) {
        AnimatedContent(
            targetState = focused,
            transitionSpec = {
                ContentTransform(
                    // Новое содержимое всплывает после того, как ушло старое — без наложения текстов.
                    targetContentEnter = fadeIn(tween(ITEM_MORPH_MS, delayMillis = ITEM_MORPH_MS / 2)),
                    initialContentExit = fadeOut(tween(ITEM_MORPH_MS / 2)),
                    sizeTransform = SizeTransform(clip = false) { _, _ -> tween(ITEM_MORPH_MS) },
                )
            },
            label = "itemMorph",
        ) { isFocused ->
            if (isFocused) {
                // Ввод уже шёл — новое поле подхватывает фокус в кадре появления (клавиатура не мигает).
                if (grabKeyboard) {
                    LaunchedEffect(Unit) { fieldFocus.requestFocus() }
                }
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceTiny)) {
                    SentenceWithBlank(
                        item = item,
                        value = value,
                        enabled = editable,
                        visual = visual,
                        focusRequester = fieldFocus,
                        onValueChange = onValueChange,
                    )
                    if (showContext && item.contextRu.isNotBlank()) {
                        Text(item.contextRu, color = TextSecondary, fontSize = 14.sp, fontStyle = FontStyle.Italic)
                    }
                    // Верный пункт не подписываем — зелёного поля достаточно; у неверного печатаем ответ.
                    if (visual == InputFieldVisual.WRONG) {
                        // Пункт с пустым правильным ответом («предлог не нужен») печатаем прочерком.
                        val dash = stringResource(R.string.exercise_correct_answer_empty)
                        ExerciseContentText(
                            text = item.answer.ifBlank { dash },
                            color = CorrectGreen,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            } else {
                CollapsedItem(sentence = item.sentence, value = value, accent = accent)
            }
        }
    }
}

/**
 * Свёрнутый пункт: точка-индикатор (заполнен — цветом [accent], пуст — [Inactive]) и превью
 * предложения в одну строку с эллипсисом. Уже вписанный ответ стоит в превью на месте пропуска
 * (жирным), пустой пропуск рисуется линией — как во всём задании ([ExerciseContentText]).
 */
@Composable
private fun CollapsedItem(sentence: String, value: String, accent: Color) {
    val filled = value.isNotBlank()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
    ) {
        Box(Modifier.size(Dimens.exerciseItemDot).background(if (filled) accent else Inactive, CircleShape))
        ExerciseContentText(
            text = previewSentence(sentence, value),
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Пропуск в предложении. Предложение без пропуска = поле идёт в конец (как в [SentenceWithBlank]). */
private val BLANK_MARKER = Regex("_{3,}")

/**
 * Текст превью: вписанный ответ подставлен на место пропуска и выделен жирным (`**…**` — разметка
 * [ExerciseContentText]), поэтому в свёрнутой строке видно не только «заполнено», но и ЧТО вписано.
 * Пока поле пустое — предложение остаётся с пропуском (он рисуется линией).
 */
private fun previewSentence(sentence: String, value: String): String {
    val word = value.trim()
    if (word.isEmpty()) return sentence
    val marked = "**$word**"
    return if (BLANK_MARKER.containsMatchIn(sentence)) {
        BLANK_MARKER.replace(sentence) { marked }
    } else {
        "$sentence $marked"
    }
}

/**
 * Свёрнутая подсказка «банк слов». Footprint (место под раскрытую капсулу) зарезервирован ВСЕГДА —
 * слова лежат на своих местах с самого начала (просто прозрачные), поэтому при раскрытии/сворачивании
 * layout не двигается. Морф — чисто визуальный: фон-капсула масштабируется от полоски к капсуле
 * ([graphicsLayer], пивот-центр → «во все стороны»), заливка `Accent → прозрачная`, рамка проявляется
 * `Inactive`, текст всплывает альфой. Всё от одного [progress]. Тап переключает; при открытии — таймер
 * на [BANK_PEEK_MS] (повторный тап перезапускает через `peekTick`). Ripple выключен.
 */
@Composable
private fun WordBankPeek(words: List<String>, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    var peekTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(peekTick) {
        if (open) {
            delay(BANK_PEEK_MS)
            open = false
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (open) 1f else 0f,
        animationSpec = tween(BANK_ANIM_MS),
        label = "bankProgress",
    )
    val shape = RoundedCornerShape(percent = 50)
    val interaction = remember { MutableInteractionSource() }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .height(BANK_CAPSULE_H)
                .clickable(interactionSource = interaction, indication = null) {
                    open = !open
                    if (open) peekTick++
                },
            contentAlignment = Alignment.Center,
        ) {
            // Фон-капсула: масштаб полоска -> капсула + плавные заливка/рамка. Размер = footprint (matchParentSize).
            Box(
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        scaleX = lerp(BANK_STRIP_SX, 1f, progress)
                        scaleY = lerp(BANK_STRIP_SY, 1f, progress)
                    }
                    .background(colorLerp(Accent, Color.Transparent, progress), shape)
                    .border(1.dp, colorLerp(Color.Transparent, Inactive, progress), shape),
            )
            // Слова резервируют место всегда; проявляются альфой во второй половине морфа.
            BankWordsText(
                words = words,
                modifier = Modifier
                    .padding(horizontal = Dimens.spaceLarge)
                    .alpha(((progress - 0.35f) / 0.65f).coerceIn(0f, 1f)),
            )
        }
    }
}

/**
 * Слова банка одной строкой по центру, разделены Material-точкой (кружок инлайн-контентом, не символ `·`).
 * Приглушённый цвет — читается как справочный набор, а не перетаскиваемые фишки [parts.ExerciseChip].
 * Одна строка (`maxLines = 1`): банк держим компактным (≤3 слов, см. exercise_templates.md).
 */
@Composable
private fun BankWordsText(words: List<String>, modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        words.forEachIndexed { i, word ->
            if (i > 0) appendInlineContent(BANK_DOT_ID, " · ")
            append(word)
        }
    }
    val inlineContent = mapOf(
        BANK_DOT_ID to InlineTextContent(
            placeholder = Placeholder(
                width = 1.6.em,
                height = 1.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
            ),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(Modifier.size(3.dp).background(TextSecondary, CircleShape))
            }
        },
    )
    Text(
        text = text,
        inlineContent = inlineContent,
        color = TextSecondary,
        fontSize = 14.sp,
        maxLines = 1,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/**
 * Предложение с инлайн-полем на месте `___`; ширина поля — по длине правильного ответа.
 * [focusRequester] висит над полем — им пункт забирает системный фокус при переключении (см.
 * `grabKeyboard` в [TextInputItem]).
 */
@Composable
private fun SentenceWithBlank(
    item: TextItem,
    value: String,
    enabled: Boolean,
    visual: InputFieldVisual,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
) {
    val annotated = buildAnnotatedString {
        val marker = item.sentence.indexOf("___")
        if (marker >= 0) {
            append(item.sentence.substring(0, marker))
            appendInlineContent(BLANK_ID, " ")
            var end = marker
            while (end < item.sentence.length && item.sentence[end] == '_') end++
            append(item.sentence.substring(end))
        } else {
            append(item.sentence)
            append(" ")
            appendInlineContent(BLANK_ID, " ")
        }
    }
    // Ширина поля растёт от длины ожидаемого ответа (em — тянется за шрифтом).
    val widthEm = (item.answer.length.coerceAtLeast(3) * 0.62f + 1.6f).em
    val inlineContent = mapOf(
        BLANK_ID to InlineTextContent(
            placeholder = Placeholder(
                width = widthEm,
                height = 1.7.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
            ),
        ) {
            ExerciseInputField(
                value = value,
                enabled = enabled,
                onValueChange = onValueChange,
                visual = visual,
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
            )
        },
    )
    Text(
        text = annotated,
        inlineContent = inlineContent,
        color = TextPrimary,
        fontSize = 20.sp,
        lineHeight = 34.sp,
    )
}