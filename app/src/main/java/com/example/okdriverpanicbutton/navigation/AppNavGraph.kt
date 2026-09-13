package com.example.okdriverpanicbutton.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.okdriverpanicbutton.data.ContactRepository
import com.example.okdriverpanicbutton.data.HistoryRepository
import com.example.okdriverpanicbutton.ui.screens.EditMessageScreen
import com.example.okdriverpanicbutton.ui.screens.HistoryScreen
import com.example.okdriverpanicbutton.ui.screens.MainMenuScreen
import com.example.okdriverpanicbutton.ui.screens.PanicScreen
import com.example.okdriverpanicbutton.ui.screens.RegisterMembersScreen
import com.example.okdriverpanicbutton.ui.screens.SplashScreen
import com.example.okdriverpanicbutton.ui.screens.ViewMembersScreen
import com.example.okdriverpanicbutton.viewmodel.PanicViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    panicViewModel: PanicViewModel
) {
    val context = LocalContext.current
    val contactRepository = ContactRepository(context)
    val historyRepository = HistoryRepository(context)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeOut(tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(tween(200))
        }
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(500)) }
        ) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.MainSOS.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.MainSOS.route) {
            PanicScreen(
                viewModel = panicViewModel,
                contactRepository = contactRepository,
                onNavigateToMenu = {
                    navController.navigate(Screen.MainMenu.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterMembers.route)
                }
            )
        }

        composable(route = Screen.RegisterMembers.route) {
            RegisterMembersScreen(
                contactRepository = contactRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.ViewMembers.route) {
            ViewMembersScreen(
                contactRepository = contactRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.MainMenu.route) {
            MainMenuScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterMembers.route)
                },
                onNavigateToViewMembers = {
                    navController.navigate(Screen.ViewMembers.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToEditMessage = {
                    navController.navigate(Screen.EditMessage.route)
                },
                contactRepository = contactRepository
            )
        }

        composable(route = Screen.History.route) {
            HistoryScreen(
                historyRepository = historyRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.EditMessage.route) {
            EditMessageScreen(
                contactRepository = contactRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
