package dev.sethan8r.grammar.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.ui.graphics.vector.ImageVector
import dev.sethan8r.grammar.app.R
import kotlinx.serialization.Serializable

/** Общий тип-маркер роутов корневых вкладок — основа белого списка навбара. */
sealed interface TopLevelRoute

@Serializable
data object LearnRoute : TopLevelRoute

@Serializable
data object PracticeRoute : TopLevelRoute

@Serializable
data object StatisticsRoute : TopLevelRoute

@Serializable
data object MenuRoute : TopLevelRoute

/**
 * Единый источник правды по корневым вкладкам: роут + подпись + иконка.
 *
 * Навбар строится итерацией по [entries]; этот же список служит белым списком
 * видимости нижней панели (см. MainScreen). Добавить, переименовать или вынести
 * вкладку = правка одной строки здесь.
 */
enum class TopLevelDestination(
    val route: TopLevelRoute,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    LEARN(LearnRoute, R.string.tab_learn, Icons.AutoMirrored.Filled.MenuBook),
    PRACTICE(PracticeRoute, R.string.tab_practice, Icons.Filled.Spellcheck),
    STATISTICS(StatisticsRoute, R.string.tab_statistics, Icons.Filled.QueryStats),
    MENU(MenuRoute, R.string.tab_menu, Icons.Filled.Settings),
}