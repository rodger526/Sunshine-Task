package com.example.sunshine_task.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateTask : Screen("create_task")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/"
    }
}
