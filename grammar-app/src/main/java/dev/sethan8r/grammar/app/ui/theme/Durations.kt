package dev.sethan8r.grammar.app.ui.theme

/**
 * Единые токены длительностей (наряду с [Dimens] и [Alphas]). Тайминги показа/анимаций берём отсюда —
 * смена поведения правит токен, не экраны. Сырые `4_000L` в Composable не пишем.
 */
object Durations {

    /** Снекбар неверного ответа в упражнении (мс) — короткий, ≈ штатной SnackbarDuration.Short. */
    const val wrongAnswerSnackbarMs = 3_000L

    /** Снекбар-описание темы/раздела по кнопке «i» (мс) — как было у всплывающего облачка. */
    const val infoSnackbarMs = 5_000L

    /** Задержка перед скрытием снекбара после отпускания пальца, если время уже вышло, пока держали (мс). */
    const val snackbarHoldGraceMs = 500L

    /** Возврат слова-чипа в пул, если отпустили не на поле сборки (WORD_ARRANGEMENT), мс. */
    const val dragReturnMs = 200

    /** Рост/сужение активного чипа нижней навигации при переключении таба, мс. */
    const val bottomBarChipGrowMs = 280

    /** Slide+fade показа/скрытия плавающей капсулы навигации при скролле, мс. */
    const val bottomBarShowHideMs = 280

    /** Смена заголовка вкладки на поле поиска и обратно, мс. */
    const val searchBarSwapMs = 220

    /** Смена тела вкладки (дерево ↔ выдача) со сдвигом вниз, мс — чуть дольше шапки. */
    const val searchBodySwapMs = 250

    /** Появление ответа AI в треде уточнения (fade + лёгкий сдвиг снизу), мс. */
    const val clarifyAnswerRevealMs = 350

    /** Схлопывание блока готовых вопросов после выбора — короче показа ответа, мс. */
    const val clarifyOptionsHideMs = 220

    /** Полный цикл «вдоха-выдоха» одной точки анимации «AI печатает», мс. */
    const val typingDotCycleMs = 900

    /** Сдвиг фазы между соседними точками — из-за него они мигают по очереди, мс. */
    const val typingDotStaggerMs = 150
}