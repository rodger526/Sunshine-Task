package com.example.sunshine_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sunshine_task.ui.task.TaskDetailScreen
import com.example.sunshine_task.ui.home.HomeScreen
import com.example.sunshine_task.ui.splash.SplashScreen
import com.example.sunshine_task.ui.theme.SunshinetaskTheme
import com.example.sunshine_task.ui.theme.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunshinetaskTheme {
                val viewModel: TaskViewModel = viewModel()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(
                            onStartClick = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onTaskClick = { taskId ->
                                navController.navigate("detail/$taskId")
                            }
                        )
                    }
                    composable(
                        route = "detail/{taskId}",
                        arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                        val task = viewModel.tasks.find { it.id == taskId }
                        if (task != null) {
                            TaskDetailScreen(
                                task = task,
                                onBack = { navController.popBackStack() },
                                onToggleStatus = { viewModel.toggleTaskStatus(task.id) },
                                onUpdateTask = { title, desc, cat, pri, due ->
                                    viewModel.updateTask(task.id, title, desc, cat, pri, due)
                                },
                                onDeleteTask = {
                                    viewModel.deleteTask(task.id)
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}
