package com.example.sunshine_task.features.statistics

import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.models.Status
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
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun calculate(tasks: List<Task>): TaskStats {
        val today = Date()

        var pendientes = 0
        var enProgreso = 0
        var completadas = 0
        var vencidas = 0

        tasks.forEach { task ->
            when (task.status) {
                Status.PENDIENTE -> pendientes++
                Status.EN_PROGRESO -> enProgreso++
                Status.COMPLETADA -> completadas++
            }

            if (task.status != Status.COMPLETADA && task.dueDate.isNotBlank()) {
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
