package dev.sethan8r.grammar.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Единые токены отступов, скруглений и размеров (8-point grid). CLAUDE.md запрещает сырые `16.dp`
 * в Composable — все размеры берём отсюда. Смена дизайна правит токены, не экраны.
 */
object Dimens {

    // Отступы (8-point grid)
    /** Плотный инсет вне 8-grid (осознанно) — для тесных чипов/бейджей (фрейм ID карточки). */
    val spaceMicro = 2.dp
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

    /**
     * Тонкий тёмный зазор скроллящегося контента перед нижней кнопкой/навбаром (и под кнопкой).
     * Конвенция Words8r: ровный 16dp оставляет толстую тёмную полосу, 8dp — тонко и аккуратно.
     */
    val bottomBarGap8 = 8.dp
    val bottomBarGap12 = 12.dp

    /** Компенсация скролла. */
    val bottomBarGap24 = 24.dp

    // Скругления
    val cornerSmall = 4.dp
    val cornerButton = 12.dp
    val cornerCard = 16.dp
    val cornerLarge = 24.dp

    // Размеры элементов
    val buttonHeight = 56.dp

    /** Фиксированная ширина бокса ID (карточки/упражнения) — чтобы 3-значные ID и «AI» не двигали полосу. */
    val idBadgeWidth = 52.dp

    /** Полоска-пропуск (`BlankBar`) — место под пропущенный ответ/реплику. Длина фиксирована (как `____`). */
    val blankLineThickness = 2.dp
    val blankLineWidth = 40.dp

    /** Толщина цветной полосы-разделителя между шапкой и телом плашки-врезки. */
    val calloutDividerThickness = 4.dp

    /** Горизонтальный отступ полосы-разделителя плашки от краёв фрейма (концы скруглены). */
    val calloutDividerInset = 16.dp

    /** Минимальная ширина ячейки таблицы (для горизонтальной прокрутки широких таблиц). */
    val tableCellMinWidth = 96.dp

    /** Фиксированная высота строки MATCHING — выравнивает закреплённую левую и переставляемую правую колонки. */
    val matchRowHeight = 52.dp

    /** Минимальная высота колонки-категории CATEGORIZATION (видна как зона сброса даже пустой). */
    val categoryColumnMinHeight = 96.dp

    /** Ширина вертикальной полосы-статуса слева в строке микротемы (зелёная — пройдена, серая — нет). */
    val microtopicStripeWidth = 4.dp

    /** Толщина обводки фрейма примера карточки. Внешний силуэт списка примеров скруглён, стыки прямые. */
    val exampleBorderWidth = 1.dp

    /** Высота сегмента полосы прогресса карточек. */
    val progressBarHeight = 4.dp

    /** Высота зоны тапа по полосе прогресса (вся полоса = одна тач-область, индекс — из X). */
    val progressBarTouchHeight = 22.dp

    /** Диаметр точки-маркера текущей карточки (у левого края её деления, как stop-indicator). */
    val progressBarDot = 4.dp
}