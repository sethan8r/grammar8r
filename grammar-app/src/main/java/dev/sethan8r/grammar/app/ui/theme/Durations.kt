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
}