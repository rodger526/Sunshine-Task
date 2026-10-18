package com.example.sunshine_task.ui.navigation

/** Definición centralizada de rutas y argumentos del grafo de navegación. */
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val CREATE_TASK = "create_task"

    const val TASK_ID_ARG = "taskId"
    const val TASK_DETAIL = "detail/{$TASK_ID_ARG}"

    fun taskDetail(taskId: String) = "detail/$taskId"
}