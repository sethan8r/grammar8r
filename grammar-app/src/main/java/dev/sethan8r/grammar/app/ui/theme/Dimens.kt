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

    /** Зазор между контентом сессии и кнопкой «Проверить»/«Далее» (осознанно вне 8-grid, обкатано). */
    val sessionFooterTopGap = 20.dp

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

    /** Диаметр точки drag-хэндла в углу переставляемой ячейки MATCHING (намёк «перетаскивается»). */
    val matchDragHandleDot = 4.dp

    /** Минимальная высота колонки-категории CATEGORIZATION (видна как зона сброса даже пустой). */
    val categoryColumnMinHeight = 96.dp

    /** Минимальная высота поля сборки WORD_ARRANGEMENT (~2 строки чипов — видно зону сброса пустой). */
    val arrangementFieldMinHeight = 100.dp

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

    // Нижняя навигация — плавающая капсула (вариант 2a). Спека: tasks/README.md.
    /** Горизонтальный отступ капсулы от краёв экрана. */
    val bottomBarFloatingMargin = 14.dp

    /** Зазор капсулы над системным nav bar. */
    val bottomBarFloatingBottomGap = 18.dp

    /** Скругление контейнера-капсулы (все углы). */
    val bottomBarContainerRadius = 26.dp

    /** Внутренний padding контейнера-капсулы. */
    val bottomBarContainerPadding = 8.dp

    /** Скругление активного чипа (залитый акцентом таб). */
    val bottomBarChipRadius = 20.dp

    /** Фиксированная высота чипа таба — закреплена, чтобы высота капсулы не дёргалась при переключении. */
    val bottomBarChipHeight = 40.dp

    /** Горизонтальный padding содержимого чипа (воздух вокруг иконки/подписи). */
    val bottomBarChipHorizontalPadding = 12.dp

    /** Зазор иконка ↔ подпись внутри активного чипа. */
    val bottomBarChipGap = 6.dp

    /** Размер иконки таба (оба состояния). */
    val bottomBarIconSize = 20.dp

    /** Радиус размытия ореола-тени вокруг капсулы (толщина чёрной рамки, растекающейся в прозрачность). */
    val bottomBarHaloBlur = 4.dp

    /** Мёртвая зона скролла для анти-дребезга показа/скрытия бара. */
    val bottomBarScrollDeadZone = 4.dp

    /** Порог «у самого верха»: ниже него бар всегда показан. */
    val bottomBarScrollTopThreshold = 48.dp

    /** Оценка высоты капсулы — для нижнего клиренса скроллящихся вкладок (контент не под баром). */
    val bottomBarFloatingHeight = 52.dp
}