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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import dev.sethan8r.grammar.app.ui.components.Grammar8rBottomBar
import dev.sethan8r.grammar.app.ui.navigation.ExerciseSessionRoute
import dev.sethan8r.grammar.app.ui.navigation.LearnRoute
import dev.sethan8r.grammar.app.ui.navigation.MenuRoute
import dev.sethan8r.grammar.app.ui.navigation.MicrotopicRoute
import dev.sethan8r.grammar.app.ui.navigation.MicrotopicSummaryRoute
import dev.sethan8r.grammar.app.ui.navigation.PracticeRoute
import dev.sethan8r.grammar.app.ui.navigation.StatisticsRoute
import dev.sethan8r.grammar.app.ui.navigation.TopLevelDestination
import dev.sethan8r.grammar.app.ui.navigation.TopicRoute
import dev.sethan8r.grammar.app.ui.screens.exercise.ExerciseSessionScreen
import dev.sethan8r.grammar.app.ui.screens.exercise.MicrotopicSummaryScreen
import dev.sethan8r.grammar.app.ui.screens.menu.MenuScreen
import dev.sethan8r.grammar.app.ui.screens.practice.PracticeScreen
import dev.sethan8r.grammar.app.ui.screens.statistics.StatisticsScreen
import dev.sethan8r.grammar.app.ui.screens.theory.MicrotopicScreen
import dev.sethan8r.grammar.app.ui.screens.theory.TheoryScreen
import dev.sethan8r.grammar.app.ui.screens.theory.TopicScreen
import dev.sethan8r.grammar.app.ui.theme.Background
import dev.sethan8r.grammar.app.ui.theme.Grammar8rTheme

/** Ключ savedStateHandle: id микротемы, к которой нужно проскроллить список после её завершения. */
private const val FOCUS_MICROTOPIC_KEY = "focusMicrotopicId"

/** Ключ savedStateHandle: id только что пройденной карточки — листалка микротемы перейдёт на следующую. */
private const val ADVANCE_AFTER_CARD_KEY = "advanceAfterCardId"

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
            composable<LearnRoute> {
                TheoryScreen(onTopicClick = { topicId -> navController.navigate(TopicRoute(topicId)) })
            }
            composable<PracticeRoute> { PracticeScreen() }
            composable<StatisticsRoute> { StatisticsScreen() }
            composable<MenuRoute> { MenuScreen() }

            composable<TopicRoute> { entry ->
                val focusId by entry.savedStateHandle
                    .getStateFlow<Int?>(FOCUS_MICROTOPIC_KEY, null)
                    .collectAsState()
                TopicScreen(
                    onMicrotopicClick = { id -> navController.navigate(MicrotopicRoute(id)) },
                    onBack = { navController.popBackStack() },
                    focusMicrotopicId = focusId,
                    onFocusConsumed = { entry.savedStateHandle[FOCUS_MICROTOPIC_KEY] = null },
                )
            }
            composable<MicrotopicRoute> { entry ->
                val advanceAfterCardId by entry.savedStateHandle
                    .getStateFlow<Int?>(ADVANCE_AFTER_CARD_KEY, null)
                    .collectAsState()
                MicrotopicScreen(
                    onBack = { navController.popBackStack() },
                    onStartExercises = { cardId -> navController.navigate(ExerciseSessionRoute(cardId)) },
                    advanceAfterCardId = advanceAfterCardId,
                    onAdvanceConsumed = { entry.savedStateHandle[ADVANCE_AFTER_CARD_KEY] = null },
                )
            }
            composable<ExerciseSessionRoute> {
                ExerciseSessionScreen(
                    onFinished = { completion ->
                        if (completion.microtopicCompleted) {
                            // Микротема пройдена → экран сводки; сессию и список карточек убираем из стека.
                            navController.navigate(MicrotopicSummaryRoute(completion.microtopicId)) {
                                popUpTo<MicrotopicRoute> { inclusive = true }
                            }
                        } else {
                            // Ещё есть карточки → возвращаемся в микротему и листаем на следующую.
                            navController.previousBackStackEntry
                                ?.savedStateHandle?.set(ADVANCE_AFTER_CARD_KEY, completion.cardId)
                            navController.popBackStack()
                        }
                    },
                    onExit = { navController.popBackStack() },
                )
            }
            composable<MicrotopicSummaryRoute> { entry ->
                MicrotopicSummaryScreen(
                    onContinue = {
                        // Назад в список микротем, наведённый на пройденную (запрос — соседней записи стека).
                        val microtopicId = entry.toRoute<MicrotopicSummaryRoute>().microtopicId
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set(FOCUS_MICROTOPIC_KEY, microtopicId)
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}