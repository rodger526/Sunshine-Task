package com.example.sunshine_task.features.filters

import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.models.Status
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TaskFilter {
    TODAS,
    PENDIENTES,
    EN_PROGRESO,
    COMPLETADAS,
    VENCIDAS
}

object TaskFilters {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun filter(tasks: List<Task>, filter: TaskFilter): List<Task> {
        val today = Date()

        return when (filter) {
            TaskFilter.TODAS -> tasks
            TaskFilter.PENDIENTES -> tasks.filter { it.status == Status.PENDIENTE }
            TaskFilter.EN_PROGRESO -> tasks.filter { it.status == Status.EN_PROGRESO }
            TaskFilter.COMPLETADAS -> tasks.filter { it.status == Status.COMPLETADA }
            TaskFilter.VENCIDAS -> tasks.filter { task ->
                if (task.status == Status.COMPLETADA || task.dueDate.isBlank()) {
                    false
                } else {
                    try {
                        val parsed = dateFormat.parse(task.dueDate)
                        parsed != null && parsed.before(today)
                    } catch (_: Exception) {
                        false
                    }
                }
            }
        }
    }
}
