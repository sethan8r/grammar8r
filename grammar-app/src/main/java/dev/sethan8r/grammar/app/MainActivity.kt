package dev.sethan8r.grammar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.sethan8r.grammar.app.ui.components.Grammar8rBottomBar
import dev.sethan8r.grammar.app.ui.navigation.LearnRoute
import dev.sethan8r.grammar.app.ui.navigation.MenuRoute
import dev.sethan8r.grammar.app.ui.navigation.PracticeRoute
import dev.sethan8r.grammar.app.ui.navigation.StatisticsRoute
import dev.sethan8r.grammar.app.ui.navigation.TopLevelDestination
import dev.sethan8r.grammar.app.ui.screens.menu.MenuScreen
import dev.sethan8r.grammar.app.ui.screens.practice.PracticeScreen
import dev.sethan8r.grammar.app.ui.screens.statistics.StatisticsScreen
import dev.sethan8r.grammar.app.ui.screens.theory.TheoryScreen
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Grammar8rTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Grammar8rTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Белый список: нижняя панель видна только на корневых вкладках. Любой другой
    // (полноэкранный) роут — подэкраны теории, сессия упражнений — появится без навбара.
    val showBottomBar = currentDestination?.hierarchy?.any { navDestination ->
        TopLevelDestination.entries.any { navDestination.hasRoute(it.route::class) }
    } == true

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                Grammar8rBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LearnRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable<LearnRoute> { TheoryScreen() }
            composable<PracticeRoute> { PracticeScreen() }
            composable<StatisticsRoute> { StatisticsScreen() }
            composable<MenuRoute> { MenuScreen() }
        }
    }
}