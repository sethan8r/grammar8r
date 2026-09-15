package dev.sethan8r.grammar.app.ui.components.exercise

import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseChip
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseDivider
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseExplanation
import dev.sethan8r.grammar.app.ui.components.exercise.parts.ExerciseFrame
import dev.sethan8r.grammar.app.ui.components.exercise.parts.PackedPool
import dev.sethan8r.grammar.app.ui.components.exercise.parts.detectChipDrag
import dev.sethan8r.grammar.app.ui.components.exercise.parts.hitTest
import dev.sethan8r.grammar.app.ui.components.exercise.parts.rememberPackedPoolState

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.ui.screens.exercise.AnswerPhase
import dev.sethan8r.grammar.app.ui.screens.exercise.isEditable
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Alphas
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.CorrectGreen
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Elevated
import dev.sethan8r.grammar.app.ui.theme.Inactive
import dev.sethan8r.grammar.app.ui.theme.IncorrectRed
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.util.animatePlacement
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.roundToInt

/** Элемент категоризации: стабильный id, текст и индекс правильной колонки. */
private data class CatItem(val id: Int, val text: String, val correctCol: Int)

/** Ячеек в строке пула — всегда две, независимо от числа категорий. */
private const val POOL_COLS = 2

/**
 * Рендерер CATEGORIZATION во [ExerciseFrame]: колонки-категории сверху, пул снизу, drag элементов
 * пул ↔ колонки (туда-обратно). Жест — на КОНТЕЙНЕРЕ (хит-тест элемента под пальцем), оверлей —
 * сиблингом фрейма (поверх клипа); зона сброса определяется по координатам колонок/пула. Ширина
 * оверлея анимированно подгоняется под слот под пальцем (колонка уже слота пула, когда категорий
 * три) — приземляется чип уже нужного размера, без скачка. Пул на старте собран по высоте чипов
 * ([PackedPool]): в строке стоят чипы одной высоты, разновысокие строки — внизу. Вердикт
 * all-or-nothing: на реванше каждый элемент уезжает в свою колонку (показ ответа), при этом изначально
 * верно лежавшие — зелёные, ошибочные — красные (видно и правильный ответ, и где была ошибка).
 */
@Composable
fun CategorizationExerciseView(
    exercise: Exercise.Categorization,
    phase: AnswerPhase,
    shakeKey: Int,
    pulseKey: Int,
    onPlacementChanged: (Map<String, Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editable = phase.isEditable
    val solved = phase == AnswerPhase.CORRECT
    val revealed = phase == AnswerPhase.REVEALED
    val density = LocalDensity.current

    // Плоский список элементов (стабильные id), перемешан для пула один раз.
    val items = remember(exercise.id) {
        exercise.categories.flatMapIndexed { col, category ->
            category.items.map { text -> text to col }
        }.mapIndexed { i, (text, col) -> CatItem(id = i, text = text, correctCol = col) }
            .shuffled()
    }
    val textById = remember(exercise.id) { items.associate { it.id to it.text } }

    // Раскладка: id элемента → индекс колонки. Отсутствует в карте ⟺ элемент в пуле.
    val placement = remember(exercise.id) { mutableStateMapOf<Int, Int>() }

    val itemById = remember(exercise.id) { items.associateBy { it.id } }
    val itemIds = remember(exercise.id) { items.map { it.id } }
    val pool = rememberPackedPoolState(keys = itemIds, columns = POOL_COLS)

    // Порядок внутри колонки: новый сброшенный элемент — в конец.
    val order = remember(exercise.id) {
        mutableStateMapOf<Int, Int>().apply { items.forEachIndexed { i, item -> put(item.id, i) } }
    }
    var nextSeq by remember(exercise.id) { mutableIntStateOf(items.size) }

    // Геометрия в координатах внешней обёртки.
    var wrapperCoords by remember(exercise.id) { mutableStateOf<LayoutCoordinates?>(null) }
    val zoneBounds = remember(exercise.id) { mutableStateMapOf<Int, Rect>() } // ключ: индекс колонки (пул — не зона)
    val centers = remember(exercise.id) { mutableStateMapOf<Int, Offset>() }
    val sizes = remember(exercise.id) { mutableStateMapOf<Int, IntSize>() }

    var dragging by remember(exercise.id) { mutableStateOf<CatItem?>(null) }
    var pointer by remember(exercise.id) { mutableStateOf(Offset.Zero) }
    var draggedSize by remember(exercise.id) { mutableStateOf(IntSize.Zero) }
    // Живой размер оверлея: он меняется на лету (чип подгоняется под слот), поэтому центр под пальцем
    // считаем по фактическому замеру, а не по размеру на старте жеста.
    var overlaySize by remember(exercise.id) { mutableStateOf(IntSize.Zero) }
    // Ширина чипа в колонке и в слоте пула — считаются в BoxWithConstraints, нужны оверлею, который
    // живёт снаружи фрейма. При двух категориях совпадают, при трёх колонка заметно уже.
    var dragColumnWidth by remember(exercise.id) { mutableStateOf(0.dp) }
    var dragPoolWidth by remember(exercise.id) { mutableStateOf(0.dp) }
    var hoverZone by remember(exercise.id) { mutableStateOf<Int?>(null) }
    // Плавный «переезд» оверлея к новому месту чипа после сброса (колонка/пул — разные родители, поэтому
    // animatePlacement тут не срабатывает; анимируем оверлей, как возврат в пул в WORD_ARRANGEMENT).
    var releasing by remember(exercise.id) { mutableStateOf<CatItem?>(null) }
    // Был ли это переезд в ДРУГУЮ зону: при возврате в ту же зону слот остаётся тёмным плейсхолдером.
    var releaseZoneChanged by remember(exercise.id) { mutableStateOf(false) }
    val releaseAnim = remember(exercise.id) { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scope = rememberCoroutineScope()

    // Элементы, которые на момент проверки стояли НЕ в своей колонке (для подсветки на реванше).
    var revealWrong by remember(exercise.id) { mutableStateOf<Set<Int>>(emptySet()) }

    fun commit() = onPlacementChanged(placement.entries.associate { (id, col) -> textById.getValue(id) to col })

    // Реванш: зафиксировать ошибки, затем разложить всё по правильным колонкам (показ ответа).
    LaunchedEffect(revealed) {
        if (!revealed) return@LaunchedEffect
        revealWrong = items.filter { placement[it.id] != it.correctCol }.map { it.id }.toSet()
        items.forEach { placement[it.id] = it.correctCol }
    }

    fun zoneAt(pos: Offset): Int? = zoneBounds.entries.firstOrNull { it.value.contains(pos) }?.key

    fun itemAt(pos: Offset): CatItem? = items.firstOrNull { item ->
        val c = centers[item.id]; val s = sizes[item.id]
        c != null && s != null && hitTest(c, s, pos)
    }

    fun chipVisual(item: CatItem): Pair<Color, Color> {
        val wasWrong = item.id in revealWrong
        val border = when {
            solved -> CorrectGreen
            revealed && wasWrong -> IncorrectRed
            revealed -> CorrectGreen
            // Приземление в ДРУГУЮ зону — без рамки (место не помечаем).
            item == releasing && releaseZoneChanged -> Color.Transparent
            else -> Inactive
        }
        val fill = when {
            solved -> CorrectGreen.copy(alpha = Alphas.answerFill)
            revealed && wasWrong -> IncorrectRed.copy(alpha = Alphas.answerFill)
            revealed -> CorrectGreen.copy(alpha = Alphas.answerFill)
            // Приземление в другую зону — пусто: карточку «привозит» оверлей, место не помечаем.
            item == releasing && releaseZoneChanged -> Color.Transparent
            // Тёмный плейсхолдер: пока тащим (откуда взяли) И пока возвращается в ту же зону (остаётся на месте).
            item == dragging || item == releasing -> Background
            else -> Elevated
        }
        return border to fill
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { wrapperCoords = it }
            // Drag элементов, дружащий со скроллом: стартует только на чипе (см. detectChipDrag); на
            // пустом месте фрейма жест уходит родительскому скроллу.
            .pointerInput(exercise.id, editable) {
                if (!editable) return@pointerInput
                detectChipDrag(
                    hitTest = { itemAt(it) },
                    onStart = { item ->
                        dragging = item
                        pointer = centers[item.id] ?: Offset.Zero
                        draggedSize = sizes[item.id] ?: IntSize.Zero
                        overlaySize = IntSize.Zero
                    },
                    onDrag = { delta ->
                        pointer += delta
                        hoverZone = zoneAt(pointer)
                    },
                    onEnd = {
                        val item = dragging
                        if (item != null) {
                            // Брошено на колонку → в неё; иначе (мимо колонок) → возврат в пул (как WORD_ARRANGEMENT).
                            val fromCol = placement[item.id]
                            val toCol = zoneAt(pointer)
                            val zoneChanged = toCol != fromCol
                            val preCenter = centers[item.id] ?: pointer
                            if (toCol != null) placement[item.id] = toCol else placement.remove(item.id)
                            // В конец колонки — только при заходе в колонку.
                            if (zoneChanged && toCol != null) {
                                order[item.id] = nextSeq
                                nextSeq += 1
                            }
                            if (fromCol == null && toCol != null) {
                                pool.take(item.id)
                            } else if (fromCol != null && toCol == null) {
                                pool.putBack(item.id)
                            }
                            commit()
                            // Плавно «довозим» оверлей до места, где чип осядет. Сменилась зона → ждём новый
                            // центр после перераскладки (snapshotFlow); та же зона → центр прежний (preCenter).
                            releasing = item
                            releaseZoneChanged = zoneChanged
                            val from = pointer
                            scope.launch {
                                releaseAnim.snapTo(from)
                                val target = if (zoneChanged) {
                                    withTimeoutOrNull(Durations.dragReturnMs.toLong() * 2) {
                                        snapshotFlow { centers[item.id] }.first { it != null && it != preCenter }
                                    } ?: centers[item.id] ?: from
                                } else {
                                    preCenter
                                }
                                releaseAnim.animateTo(target, tween(durationMillis = Durations.dragReturnMs))
                                releasing = null
                            }
                        }
                        dragging = null
                        hoverZone = null
                    },
                )
            },
    ) {
        ExerciseFrame(shakeKey = shakeKey, pulseKey = pulseKey) {
            Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
                Text(
                    text = exercise.taskDescription.replaceFirstChar { it.uppercase() },
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                )
            }

            ExerciseDivider()

            Column(modifier = Modifier.padding(horizontal = Dimens.cardPadding)) {
                BoxWithConstraints {
                    val n = exercise.categories.size
                    // Ширина чипа = ширина внутренней области колонки (общая для колонок и пула — пресайз).
                    val columnWidth = (maxWidth - Dimens.spaceSmall * (n - 1)) / n
                    val chipWidth = columnWidth - Dimens.spaceSmall * 2
                    // Пул всегда в два слота половинной ширины — при двух категориях его чипы стоят
                    // ровно под чипами колонок, при трёх остаются такими же широкими.
                    val poolSlotWidth = (maxWidth - Dimens.spaceSmall) / POOL_COLS
                    val poolChipWidth = poolSlotWidth - Dimens.spaceSmall * 2
                    SideEffect {
                        dragColumnWidth = chipWidth
                        dragPoolWidth = poolChipWidth
                    }

                    Column {
                        // --- Колонки-категории ---
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                            exercise.categories.forEachIndexed { col, category ->
                                CategoryColumn(
                                    title = category.title,
                                    width = columnWidth,
                                    highlighted = hoverZone == col,
                                    onBoundsChanged = { rect -> zoneBounds[col] = rect },
                                    wrapperCoords = wrapperCoords,
                                ) {
                                    items.filter { placement[it.id] == col }
                                        .sortedBy { order[it.id] ?: 0 }
                                        .forEach { item ->
                                        val (border, fill) = chipVisual(item)
                                        key(item.id) {
                                            CatChip(
                                                text = item.text,
                                                width = chipWidth,
                                                background = fill,
                                                border = border,
                                                contentAlpha = if (item == dragging || item == releasing) 0f else 1f,
                                                onGeometry = { c, s -> centers[item.id] = c; sizes[item.id] = s },
                                                wrapperCoords = wrapperCoords,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(Dimens.spaceXLarge))

                        // --- Пул (нераспределённые): слоты фикс. ширины, изъятый оставляет дыру, пустая
                        // строка не занимает места. На реванше placement проставлен всем — пул схлопывается
                        // целиком. Пул как «зона» не регистрируется: брошенный мимо колонок возвращается сюда.
                        PackedPool(
                            state = pool,
                            slotWidth = poolSlotWidth,
                            horizontalSpacing = Dimens.spaceSmall,
                            verticalSpacing = Dimens.spaceSmall,
                            isVisible = { id -> placement[id] == null },
                            modifier = Modifier.fillMaxWidth(),
                        ) { id ->
                            val item = itemById.getValue(id)
                            val (border, fill) = chipVisual(item)
                            CatChip(
                                text = item.text,
                                width = poolChipWidth,
                                background = fill,
                                border = border,
                                contentAlpha = if (item == dragging || item == releasing) 0f else 1f,
                                onGeometry = { c, s -> centers[item.id] = c; sizes[item.id] = s },
                                wrapperCoords = wrapperCoords,
                            )
                        }
                    }
                }
            }

            ExerciseExplanation(phase = phase, text = exercise.explanation)
        }

        // Оверлей перетаскиваемого/«переезжающего» элемента — поверх фрейма (вне его клипа). Под пальцем
        // при drag; после сброса плавно едет к новому месту чипа (releaseAnim). Прямой ExerciseChip
        // (без animatePlacement/захвата геометрии — позицию держит offset).
        (dragging ?: releasing)?.let { item ->
            val pos = if (dragging != null) pointer else releaseAnim.value
            // Слот колонки уже слота пула (при трёх категориях), поэтому ширину подгоняем на лету: чип
            // сужается, ещё зависая над колонкой, и приземляется уже нужного размера — без скачка.
            val overColumn = if (dragging != null) hoverZone != null else placement[item.id] != null
            val slotWidth = (if (overColumn) dragColumnWidth else dragPoolWidth)
                .takeIf { it > 0.dp } ?: with(density) { draggedSize.width.toDp() }
            val chipWidth by animateDpAsState(
                targetValue = slotWidth,
                animationSpec = tween(durationMillis = Durations.dragResizeMs),
                label = "categorizationDragChipWidth",
            )
            ExerciseChip(
                text = item.text,
                background = Elevated,
                border = Inactive,
                modifier = Modifier
                    .zIndex(1f)
                    .width(chipWidth)
                    .onSizeChanged { overlaySize = it }
                    .offset {
                        val w = if (overlaySize.width > 0) overlaySize.width else draggedSize.width
                        val h = if (overlaySize.height > 0) overlaySize.height else draggedSize.height
                        IntOffset(
                            (pos.x - w / 2f).roundToInt(),
                            (pos.y - h / 2f).roundToInt(),
                        )
                    },
            )
        }
    }
}

/** Колонка-категория: заголовок + зона сброса фикс. ширины; рамка-акцент при наведении перетаскиванием. */
@Composable
private fun CategoryColumn(
    title: String,
    width: Dp,
    highlighted: Boolean,
    onBoundsChanged: (Rect) -> Unit,
    wrapperCoords: LayoutCoordinates?,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(width)
            .heightIn(min = Dimens.categoryColumnMinHeight)
            .clip(RoundedCornerShape(Dimens.cornerButton))
            .border(
                width = 1.dp,
                color = if (highlighted) Accent else Inactive,
                shape = RoundedCornerShape(Dimens.cornerButton),
            )
            .onGloballyPositioned { ac ->
                val w = wrapperCoords ?: return@onGloballyPositioned
                val tl = w.localPositionOf(ac, Offset.Zero)
                onBoundsChanged(Rect(tl, Size(ac.size.width.toFloat(), ac.size.height.toFloat())))
            }
            .padding(Dimens.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        content()
    }
}

/** Чип-элемент категоризации фикс. ширины [width] (пресайз под колонку). Контентный EN ([ExerciseChip]). */
@Composable
private fun CatChip(
    text: String,
    width: Dp,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier,
    contentAlpha: Float = 1f,
    onGeometry: ((Offset, IntSize) -> Unit)? = null,
    wrapperCoords: LayoutCoordinates? = null,
) {
    ExerciseChip(
        text = text,
        background = background,
        border = border,
        contentAlpha = contentAlpha,
        modifier = modifier
            .width(width)
            .animatePlacement()
            .onGloballyPositioned { coords ->
                val w = wrapperCoords ?: return@onGloballyPositioned
                val s = coords.size
                onGeometry?.invoke(
                    w.localPositionOf(coords, Offset(s.width / 2f, s.height / 2f)),
                    s,
                )
            },
    )
}