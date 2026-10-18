package com.example.sunshine_task.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sunshine_task.ui.home.HomeRoute
import com.example.sunshine_task.ui.task.CreateTaskRoute
import com.example.sunshine_task.ui.task.TaskDetailRoute

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeRoute(
                onNavigateToCreate = { navController.navigate(Screen.CreateTask.route) },
                onNavigateToDetail = { taskId -> navController.navigate(Screen.TaskDetail.createRoute(taskId)) }
            )
        }
        composable(Screen.CreateTask.route) {
            CreateTaskRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) {
            TaskDetailRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
