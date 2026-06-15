package dev.sethan8r.grammar.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Единые токены отступов, скруглений и размеров (8-point grid). CLAUDE.md запрещает сырые `16.dp`
 * в Composable — все размеры берём отсюда. Смена дизайна правит токены, не экраны.
 */
object Dimens {

    // Отступы (8-point grid)
    val spaceTiny = 4.dp
    val spaceSmall = 8.dp
    val spaceMedium = 12.dp
    val spaceLarge = 16.dp
    val spaceXLarge = 24.dp
    val spaceXXLarge = 32.dp

    /** Стандартный горизонтальный отступ экрана от краёв. */
    val screenPadding = 16.dp

    /** Внутренний отступ карточки. */
    val cardPadding = 16.dp

    // Скругления
    val cornerButton = 12.dp
    val cornerCard = 16.dp
    val cornerLarge = 24.dp

    // Размеры элементов
    val buttonHeight = 56.dp

    /** Толщина цветной полосы-разделителя между шапкой и телом плашки-врезки. */
    val calloutDividerThickness = 4.dp

    /** Горизонтальный отступ полосы-разделителя плашки от краёв фрейма (концы скруглены). */
    val calloutDividerInset = 16.dp

    /** Минимальная ширина ячейки таблицы (для горизонтальной прокрутки широких таблиц). */
    val tableCellMinWidth = 96.dp

    /** Высота сегмента полосы прогресса карточек. */
    val progressBarHeight = 4.dp

    /** Максимальная ширина всплывающего облачка с описанием. */
    val bubbleMaxWidth = 280.dp
}