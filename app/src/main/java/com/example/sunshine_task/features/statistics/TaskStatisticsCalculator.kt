package com.example.sunshine_task.features.statistics

import com.example.sunshine_task.model.Task
import com.example.sunshine_task.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TaskStats(
    val total: Int,
    val pendientes: Int,
    val enProgreso: Int,
    val completadas: Int,
    val vencidas: Int
)

object TaskStatisticsCalculator {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun calculate(tasks: List<Task>): TaskStats {
        val today = Date()

        var pendientes = 0
        var enProgreso = 0
        var completadas = 0
        var vencidas = 0

        tasks.forEach { task ->
            when (task.status) {
                TaskStatus.PENDIENTE -> pendientes++
                TaskStatus.EN_PROGRESO -> enProgreso++
                TaskStatus.COMPLETADA -> completadas++
            }

            if (task.status != TaskStatus.COMPLETADA && !task.dueDate.isNullOrBlank()) {
                try {
                    val parsed = dateFormat.parse(task.dueDate)
                    if (parsed != null && parsed.before(today)) {
                        vencidas++
                    }
                } catch (_: Exception) {
                    // Ignora fechas no legibles
                }
            }
        }

        return TaskStats(
            total = tasks.size,
            pendientes = pendientes,
            enProgreso = enProgreso,
            completadas = completadas,
            vencidas = vencidas
        )
    }
}
