package com.example.sunshine_task.features.statistics

import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.features.filters.isOverdue
import java.time.LocalDate

data class TaskStats(
    val total: Int,
    val pendientes: Int,
    val enProgreso: Int,
    val completadas: Int,
    val vencidas: Int
)

object TaskStatisticsCalculator {

    /** Las estadísticas se calculan siempre a partir de las tareas; no se almacenan. */
    fun calculate(tasks: List<Task>): TaskStats {
        val today = LocalDate.now()

        return TaskStats(
            total = tasks.size,
            pendientes = tasks.count { it.status == Status.PENDIENTE },
            enProgreso = tasks.count { it.status == Status.EN_PROGRESO },
            completadas = tasks.count { it.status == Status.COMPLETADA },
            vencidas = tasks.count { it.isOverdue(today) }
        )
    }
}