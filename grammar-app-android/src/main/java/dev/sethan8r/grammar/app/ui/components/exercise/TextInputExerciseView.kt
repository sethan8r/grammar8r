package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseAccordionItem
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseContentText
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseInputField
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExercisePeekButton
import dev.sethan8r.grammar.app.ui.components.exercise.parts.InputFieldVisual

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp as colorLerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.lerp as lerpDp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.core.model.exercise.Exercise
import dev.sethan8r.grammar.core.model.exercise.ExerciseAnswer
import dev.sethan8r.grammar.core.model.exercise.TextItem
import dev.sethan8r.grammar.core.usecase.ExerciseEvaluator
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.TextPrimary
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.util.isImeVisible
import kotlinx.coroutines.delay

private const val BLANK_ID = "blank"
private const val BANK_DOT_ID = "bankDot"
private const val PEEK_ID = "peek"

/** Инлайн-слот глазка сразу за полем: ширина в em — тянется за шрифтом предложения, как и поле. */
private val PEEK_SLOT_EM = 1.8.em

/** Банк-подсказка: держится раскрытым, мс; длительность морфа, мс. */
private const val BANK_PEEK_MS = 5_000L
private const val BANK_ANIM_MS = 260

/** Минимальная высота footprint'а капсулы (место резервируется всегда — при раскрытии ничего не двигается). */
private val BANK_CAPSULE_H = 32.dp

/** Сколько строк вмещает капсула: длинный банк переносится, а не обрезается. */
private const val BANK_MAX_LINES = 3

/** Размер свёрнутой полоски — одинаков во всех заданиях, от длины банка не зависит. */
private val BANK_STRIP_W = 56.dp
private val BANK_STRIP_H = 5.dp

/** Пробел внутри элемента банка — по нему строка не рвётся (U+00A0). */
private const val NO_BREAK_SPACE = '\u00A0'

/** Разрешённое место переноса между элементами банка, шириной ноль (U+200B). */
private const val LINE_BREAK_OPPORTUNITY = "\u200B"

/**
 * Рендерер TEXT_INPUT во [ExerciseFrame] (трясётся на ошибке). Пропуск `___` — это инлайн-поле ввода
 * прямо в предложении (пользователь печатает в него, а не в отдельное поле), ширина — по длине
 * правильного ответа. Проверка ввода — case-insensitive с учётом сокращений
 * ([dev.sethan8r.grammar.core.usecase.AnswerNormalizer]).
 *
 * Раскладка — аккордеон-фокус на общем [ExerciseAccordionItem]: пока идёт ответ, раскрыт ровно ОДИН
 * пункт (предложение с полем + контекст), остальные свёрнуты в строку «точка-индикатор + приглушённое
 * превью, где вписанное слово стоит на месте пропуска». Тап по свёрнутой строке переводит фокус на
 * неё — так задание из 4–5 пунктов не растёт в простыню. Когда всё верно, акцент пунктов становится
 * зелёным. На реванше ([AnswerPhase.REVEALED]) аккордеон выключается: раскрыты все пункты, поле
 * каждого окрашено по своему результату (зелёная/красная рамка), а у ошибочного рядом с полем стоит
 * глазок — он подменяет в поле ответ пользователя эталоном и обратно (отдельной строки с правильным
 * ответом больше нет). Вердикт задания при этом остаётся all-or-nothing (его держит экран сессии).
 *
 * Режим «банк слов» (когда есть [Exercise.TextInput.wordBank]): сверху — шапка-задание +
 * [ExerciseDivider], а В САМОМ НИЗУ — [WordBankPeek]: голубая полоска, которая по тапу «разъезжается»
 * в серую капсулу со словами (масштаб от центра + цвет + альфа текста, footprint зарезервирован —
 * ничего не сдвигается) на [BANK_PEEK_MS] и сворачивается обратно. Высота капсулы идёт по содержимому
 * (от [BANK_CAPSULE_H]): длинный набор переносится на несколько строк. Пер-пунктовые глоссы (contextRu)
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
    val imeVisible = isImeVisible()

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
 * Один пункт задания: свёрнут — строка-превью предложения (общий [ExerciseAccordionItem]), раскрыт —
 * предложение с инлайн-полем и контекстом ([showContext]) под ним. У ошибочного пункта на реванше
 * рядом с полем встаёт глазок: он подменяет в поле ответ пользователя правильным и обратно.
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
    ExerciseAccordionItem(
        focused = focused,
        filled = value.isNotBlank(),
        accent = accent,
        outlined = visual == InputFieldVisual.NEUTRAL,
        grabKeyboard = grabKeyboard,
        preview = previewSentence(item.sentence, value),
        onFocus = onFocus,
    ) { fieldFocus ->
        var peekCorrect by remember { mutableStateOf(false) }
        val wrong = visual == InputFieldVisual.WRONG
        // Пункт с пустым правильным ответом («предлог не нужен») показываем прочерком.
        val dash = stringResource(R.string.exercise_correct_answer_empty)
        val peeking = wrong && peekCorrect

        SentenceWithBlank(
            item = item,
            value = if (peeking) item.answer.ifBlank { dash } else value,
            enabled = editable,
            // Пока показан правильный ответ, поле зелёное — видно, что это эталон, а не твой ввод.
            visual = if (peeking) InputFieldVisual.CORRECT else visual,
            showPeek = wrong,
            peeking = peekCorrect,
            focusRequester = fieldFocus,
            onValueChange = onValueChange,
            onPeekToggle = { peekCorrect = !peekCorrect },
        )
        if (showContext && item.contextRu.isNotBlank()) {
            Text(item.contextRu, color = TextSecondary, fontSize = 14.sp, fontStyle = FontStyle.Italic)
        }
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
 * layout не двигается. Морф идёт по РАЗМЕРУ фона: от полоски [BANK_STRIP_W] × [BANK_STRIP_H] к замеренному
 * размеру капсулы, из центра «во все стороны», вместе с заливкой `Accent → прозрачная` и проявлением
 * рамки `Inactive`; текст всплывает альфой. Всё от одного [progress]. Размер, а не масштаб — потому что
 * скругление у растянутого слоя сплющивается тем сильнее, чем выше капсула, а свёрнутая полоска обязана
 * выглядеть одинаково при любой длине банка. Тап переключает; при открытии — таймер на [BANK_PEEK_MS]
 * (повторный тап перезапускает через `peekTick`). Ripple выключен.
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
    val density = LocalDensity.current
    // Размер раскрытой капсулы — из замера footprint'а: фон меняет РЕАЛЬНЫЙ размер, а не масштаб,
    // поэтому скругление не сплющивается и свёрнутая полоска везде одна и та же.
    var capsule by remember { mutableStateOf(IntSize.Zero) }
    val fullSize = with(density) { DpSize(capsule.width.toDp(), capsule.height.toDp()) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .heightIn(min = BANK_CAPSULE_H)
                .onSizeChanged { capsule = it }
                .clickable(interactionSource = interaction, indication = null) {
                    open = !open
                    if (open) peekTick++
                },
            contentAlignment = Alignment.Center,
        ) {
            // Фон: полоска -> капсула (размер + плавные заливка/рамка). Footprint держит текст, не фон.
            Box(
                Modifier
                    .size(
                        width = lerpDp(BANK_STRIP_W, fullSize.width.coerceAtLeast(BANK_STRIP_W), progress),
                        height = lerpDp(BANK_STRIP_H, fullSize.height.coerceAtLeast(BANK_STRIP_H), progress),
                    )
                    .background(colorLerp(Accent, Color.Transparent, progress), shape)
                    .border(1.dp, colorLerp(Color.Transparent, Inactive, progress), shape),
            )
            // Слова резервируют место всегда; проявляются альфой во второй половине морфа.
            BankWordsText(
                words = words,
                modifier = Modifier
                    .padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceTiny)
                    .alpha(((progress - 0.35f) / 0.65f).coerceIn(0f, 1f)),
            )
        }
    }
}

/**
 * Слова банка по центру, разделены Material-точкой (кружок инлайн-контентом, не символ `·`).
 * Приглушённый цвет — читается как справочный набор, а не перетаскиваемые фишки [parts.ExerciseChip].
 * Набор длиннее строки переносится (до [BANK_MAX_LINES]), и капсула подрастает под него: внутри
 * элемента пробелы неразрывные, а точки разрыва стоят только по краям точки-разделителя — поэтому
 * строка ломается между элементами банка, а сам элемент остаётся целым.
 */
@Composable
private fun BankWordsText(words: List<String>, modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        words.forEachIndexed { i, word ->
            if (i > 0) {
                append(LINE_BREAK_OPPORTUNITY)
                appendInlineContent(BANK_DOT_ID, " · ")
                append(LINE_BREAK_OPPORTUNITY)
            }
            append(word.replace(' ', NO_BREAK_SPACE))
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
        lineHeight = 22.sp,
        maxLines = BANK_MAX_LINES,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/**
 * Предложение с инлайн-полем на месте `___`; ширина поля — по длине правильного ответа.
 * [focusRequester] висит над полем — им пункт забирает системный фокус при переключении (см.
 * `grabKeyboard` в [TextInputItem]). В самом конце предложения стоит инлайн-слот глазка — место под
 * него занято всегда (иначе вердикт переносил бы предложение), а кнопка в нём появляется по
 * [showPeek] и переключает поле «ответ ↔ эталон».
 */
@Composable
private fun SentenceWithBlank(
    item: TextItem,
    value: String,
    enabled: Boolean,
    visual: InputFieldVisual,
    showPeek: Boolean,
    peeking: Boolean,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onPeekToggle: () -> Unit,
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
        // Глазок — последним, после точки: он про весь пункт, а не про конкретное слово. Слот стоит
        // ВСЕГДА (кнопка в нём появляется по [showPeek]) — иначе вердикт переносил бы предложение.
        appendInlineContent(PEEK_ID, " ")
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
        PEEK_ID to InlineTextContent(
            placeholder = Placeholder(
                width = PEEK_SLOT_EM,
                height = 1.7.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
            ),
        ) {
            ExercisePeekButton(
                visible = showPeek,
                peeking = peeking,
                onToggle = onPeekToggle,
                modifier = Modifier.fillMaxSize(),
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
