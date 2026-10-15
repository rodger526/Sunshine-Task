package com.example.sunshine_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunshine_task.ui.task.TaskDetailScreen
import com.example.sunshine_task.ui.home.HomeScreen
import com.example.sunshine_task.ui.splash.SplashScreen
import com.example.sunshine_task.ui.theme.SunshinetaskTheme
import com.example.sunshine_task.ui.theme.viewmodel.TaskViewModel

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    data class Detail(val taskId: String) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunshinetaskTheme {
                val viewModel: TaskViewModel = viewModel()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    label = "screenTransition"
                ) { screen ->
                    when (screen) {
                        is Screen.Splash -> {
                            SplashScreen(
                                onStartClick = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.Home -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onTaskClick = { taskId -> currentScreen = Screen.Detail(taskId) }
                            )
                        }
                        is Screen.Detail -> {
                            val task = viewModel.tasks.find { it.id == screen.taskId }
                            if (task != null) {
                                TaskDetailScreen(
                                    task = task,
                                    onBack = { currentScreen = Screen.Home },
                                    onToggleStatus = { viewModel.toggleTaskStatus(task.id) },
                                    onUpdateTask = { title, desc, cat, pri, due ->
                                        viewModel.updateTask(task.id, title, desc, cat, pri, due)
                                    },
                                    onDeleteTask = {
                                        viewModel.deleteTask(task.id)
                                        currentScreen = Screen.Home
                                    }
                                )
                            } else {
                                currentScreen = Screen.Home
                            }
                        }
                    }
                }
            }
        }
    }
}
