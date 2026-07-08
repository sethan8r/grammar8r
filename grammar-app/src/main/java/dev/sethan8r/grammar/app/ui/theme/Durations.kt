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

    /** Возврат слова-чипа в пул, если отпустили не на поле сборки (WORD_ARRANGEMENT), мс. */
    const val dragReturnMs = 200

    /** Рост/сужение активного чипа нижней навигации при переключении таба, мс. */
    const val bottomBarChipGrowMs = 280

    /** Slide+fade показа/скрытия плавающей капсулы навигации при скролле, мс. */
    const val bottomBarShowHideMs = 280
}