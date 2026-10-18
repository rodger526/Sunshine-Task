package com.example.sunshine_task.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sunshine_task.ui.home.HomeRoute
import com.example.sunshine_task.ui.splash.SplashScreen
import com.example.sunshine_task.ui.task.CreateTaskRoute
import com.example.sunshine_task.ui.task.TaskDetailRoute

/** Grafo de navegación centralizado. Cada destino con estado obtiene su propio ViewModel vía Hilt. */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onStartClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeRoute(
                onTaskClick = { taskId -> navController.navigate(Routes.taskDetail(taskId)) },
                onCreateTaskClick = { navController.navigate(Routes.CREATE_TASK) }
            )
        }

        dialog(
            route = Routes.CREATE_TASK,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CreateTaskRoute(
                onTaskCreated = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TASK_DETAIL,
            arguments = listOf(navArgument(Routes.TASK_ID_ARG) { type = NavType.StringType })
        ) {
            TaskDetailRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}