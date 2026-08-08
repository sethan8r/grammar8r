package dev.sethan8r.grammar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import dev.sethan8r.grammar.app.ui.components.feedback.FeedbackSnackbarHost
import dev.sethan8r.grammar.app.ui.components.feedback.LocalTabSnackbarController
import dev.sethan8r.grammar.app.ui.components.feedback.rememberFeedbackSnackbarController
import dev.sethan8r.grammar.app.ui.components.scaffold.BottomNavScrim
import dev.sethan8r.grammar.app.ui.components.scaffold.Grammar8rBottomBar
import dev.sethan8r.grammar.app.ui.components.scaffold.TopStatusScrim
import dev.sethan8r.grammar.app.ui.components.scaffold.rememberBottomBarScrollBehavior
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
import dev.sethan8r.grammar.app.ui.theme.Dimens
import dev.sethan8r.grammar.app.ui.theme.Durations
import dev.sethan8r.grammar.app.ui.theme.Grammar8rTheme

/** Ключ savedStateHandle: id микротемы, к которой нужно проскроллить список после её завершения. */
private const val FOCUS_MICROTOPIC_KEY = "focusMicrotopicId"

/** Ключ savedStateHandle: id только что пройденной карточки — листалка микротемы перейдёт на следующую. */
private const val ADVANCE_AFTER_CARD_KEY = "advanceAfterCardId"

/**
 * Ключ savedStateHandle: вкладка «Учить» открыта переключением нижней панели, а не возвратом из
 * темы, — режим поиска нужно закрыть. Возврат «назад» из микротемы флага не ставит, поэтому там
 * запрос и выдача остаются на месте.
 */
private const val RESET_THEORY_SEARCH_KEY = "resetTheorySearch"

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
    val layoutDirection = LocalLayoutDirection.current

    // Плавающая капсула навигации парит поверх контента — прячется/показывается при скролле.
    val bottomBarScroll = rememberBottomBarScrollBehavior()

    // Общий снекбар вкладок (описания тем из «i»): висит над капсулой и едет вместе с ней.
    val tabSnackbar = rememberFeedbackSnackbarController()

    // Смена роута: капсулу показать (чтобы не «залипла» скрытой), снекбар снять
    // (описание темы не должно доживать таймер поверх чужого экрана).
    LaunchedEffect(currentDestination) {
        bottomBarScroll.forceShow()
        tabSnackbar.dismiss()
    }

    Scaffold(containerColor = Background) { innerPadding ->
        // Верхний инсет строки состояния для полноэкранных роутов. НЕ вешаем его на общий контейнер
        // ниже: контейнер держит оба экрана во время перехода/предиктив-бэка, и смена top при смене
        // роута дёргала бы вёрстку соседнего экрана (визуальный скачок). Поэтому его берёт каждый
        // полноэкранный экран сам — через [fullScreenComposable]. Значение то же, что раньше.
        val topInset = innerPadding.calculateTopPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Низ не резервируется (капсула не в Scaffold). Верх на общий контейнер не вешаем (см.
                // выше): вкладки уходят edge-to-edge под строку состояния (её перекрывает [TopStatusScrim]),
                // полноэкранные роуты держат верхний инсет сами.
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                ),
        ) {
          CompositionLocalProvider(LocalTabSnackbarController provides tabSnackbar) {
            NavHost(
                navController = navController,
                startDestination = LearnRoute,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(bottomBarScroll.nestedScrollConnection),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) {
            opaqueComposable<LearnRoute> { entry ->
                val resetSearch by entry.savedStateHandle
                    .getStateFlow(RESET_THEORY_SEARCH_KEY, false)
                    .collectAsState()
                TheoryScreen(
                    onTopicClick = { topicId -> navController.navigate(TopicRoute(topicId)) },
                    onMicrotopicClick = { id -> navController.navigate(MicrotopicRoute(id)) },
                    resetSearch = resetSearch,
                    onResetSearchConsumed = { entry.savedStateHandle[RESET_THEORY_SEARCH_KEY] = false },
                )
            }
            opaqueComposable<PracticeRoute> { PracticeScreen() }
            opaqueComposable<StatisticsRoute> { StatisticsScreen() }
            opaqueComposable<MenuRoute> { MenuScreen() }

            fullScreenComposable<TopicRoute>(topInset) { entry ->
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
            fullScreenComposable<MicrotopicRoute>(topInset) { entry ->
                val advanceAfterCardId by entry.savedStateHandle
                    .getStateFlow<Int?>(ADVANCE_AFTER_CARD_KEY, null)
                    .collectAsState()
                MicrotopicScreen(
                    onBack = { navController.popBackStack() },
                    onStartExercises = { cardId -> navController.navigate(ExerciseSessionRoute(cardId)) },
                    // Последняя карточка без заданий → сводка (как из сессии).
                    onMicrotopicCompleted = { microtopicId ->
                        navController.navigate(MicrotopicSummaryRoute(microtopicId)) {
                            popUpTo<MicrotopicRoute> { inclusive = true }
                        }
                    },
                    advanceAfterCardId = advanceAfterCardId,
                    onAdvanceConsumed = { entry.savedStateHandle[ADVANCE_AFTER_CARD_KEY] = null },
                )
            }
            fullScreenComposable<ExerciseSessionRoute>(topInset) {
                ExerciseSessionScreen(
                    onFinished = { completion ->
                        val microtopicId = completion.microtopicId
                        when {
                            // Карточки нет в content.db (рассинхрон контента) — сводки не будет, просто назад.
                            microtopicId == null -> navController.popBackStack()

                            completion.isLastCard ->
                                // Последняя карточка микротемы → экран сводки; сессию и список карточек убираем из стека.
                                navController.navigate(MicrotopicSummaryRoute(microtopicId)) {
                                    popUpTo<MicrotopicRoute> { inclusive = true }
                                }

                            else -> {
                                // Ещё есть карточки → возвращаемся в микротему и листаем на следующую.
                                navController.previousBackStackEntry
                                    ?.savedStateHandle?.set(ADVANCE_AFTER_CARD_KEY, completion.cardId)
                                navController.popBackStack()
                            }
                        }
                    },
                    onExit = { navController.popBackStack() },
                )
            }
            fullScreenComposable<MicrotopicSummaryRoute>(topInset) { entry ->
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

            // Градиент-скрим над системной полосой навигации — на всех экранах, кроме сессии
            // упражнений (там низ держит футер с кнопкой, контент под полосу не заезжает).
            // Под снекбаром и капсулой — они рисуются позже.
            val isExerciseSession = currentDestination?.hasRoute(ExerciseSessionRoute::class) == true
            if (!isExerciseSession) {
                BottomNavScrim(modifier = Modifier.align(Alignment.BottomCenter))
            }

            // Общий снекбар вкладок — над капсулой. Нижний отступ анимируется тем же спеком/флагом,
            // что и капсула: она видна → снекбар над ней; спрятана → съезжает к низу экрана (не за него).
            // Один нижний порог вместо двух независимых отступов: плашка встаёт над тем, что ниже
            // всего мешает — клавиатурой, капсулой навигации или системной полосой. Раньше инсет
            // клавиатуры и подъём под капсулу анимировались врозь, и при скрытии клавиатуры плашка
            // успевала съехать к самому низу, а затем возвращалась.
            val density = LocalDensity.current
            val imeBottom = with(density) { WindowInsets.ime.getBottom(density).toDp() }
            val navigationBottom = with(density) {
                WindowInsets.navigationBars.getBottom(density).toDp()
            }
            val capsuleFloor by animateDpAsState(
                targetValue = if (showBottomBar && bottomBarScroll.isVisible.value) {
                    navigationBottom + Dimens.bottomBarFloatingHeight + Dimens.bottomBarFloatingBottomGap
                } else {
                    navigationBottom
                },
                animationSpec = tween(Durations.bottomBarShowHideMs, easing = FastOutSlowInEasing),
                label = "tabSnackbarFloor",
            )
            FeedbackSnackbarHost(
                hostState = tabSnackbar.hostState,
                onHoldChanged = tabSnackbar::setHeld,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding)
                    .padding(bottom = maxOf(imeBottom, capsuleFloor) + Dimens.spaceLarge),
            )

            // Градиент-скрим над строкой состояния — только на вкладках (где контент уходит под неё).
            if (showBottomBar) {
                TopStatusScrim(modifier = Modifier.align(Alignment.TopCenter))
            }

            // Плавающая капсула — оверлей поверх контента (только на корневых вкладках),
            // slide+fade при скролле. Один источник видимости — [bottomBarScroll].
            if (showBottomBar) {
                AnimatedVisibility(
                    visible = bottomBarScroll.isVisible.value,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(
                        animationSpec = tween(Durations.bottomBarShowHideMs, easing = FastOutSlowInEasing),
                    ) { it } + fadeIn(tween(Durations.bottomBarShowHideMs)),
                    exit = slideOutVertically(
                        animationSpec = tween(Durations.bottomBarShowHideMs, easing = FastOutSlowInEasing),
                    ) { it } + fadeOut(tween(Durations.bottomBarShowHideMs)),
                ) {
                    Grammar8rBottomBar(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            navController.currentBackStack.value
                                .lastOrNull { it.destination.hasRoute(LearnRoute::class) }
                                ?.savedStateHandle?.set(RESET_THEORY_SEARCH_KEY, true)
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
            }
        }
    }
}

/**
 * Регистрирует полноэкранный destination с НЕПРОЗРАЧНЫМ фоном. Во время жеста «назад» (predictive
 * back) Navigation Compose держит в композиции сразу два экрана; без своего фона верхний экран
 * просвечивал бы и сквозь него был бы виден нижний (наложение). Непрозрачный [Background] под
 * каждым экраном убирает просвечивание — единая точка, новые экраны защищены автоматически.
 */
private inline fun <reified T : Any> NavGraphBuilder.opaqueComposable(
    noinline content: @Composable (NavBackStackEntry) -> Unit,
) = composable<T> { entry ->
    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        content(entry)
    }
}

/**
 * Полноэкранный destination с непрозрачным фоном И собственным верхним инсетом ([topInset] = высота
 * строки состояния). Инсет держит КАЖДЫЙ экран сам, а не общий родитель NavHost'а — иначе при
 * переходе/предиктив-бэке смена инсета дёргала бы вёрстку соседнего экрана (скачок). Значение то же,
 * что раньше давал общий контейнер, просто применено пер-экранно, поэтому вёрстка не прыгает.
 */
private inline fun <reified T : Any> NavGraphBuilder.fullScreenComposable(
    topInset: Dp,
    noinline content: @Composable (NavBackStackEntry) -> Unit,
) = composable<T> { entry ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(top = topInset),
    ) {
        content(entry)
    }
}