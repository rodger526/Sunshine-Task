package com.example.sunshine_task.features.filters

import com.example.sunshine_task.model.Task
import com.example.sunshine_task.model.TaskStatus
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
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun filter(tasks: List<Task>, filter: TaskFilter): List<Task> {
        val today = Date()

        return when (filter) {
            TaskFilter.TODAS -> tasks
            TaskFilter.PENDIENTES -> tasks.filter { it.status == TaskStatus.PENDIENTE }
            TaskFilter.EN_PROGRESO -> tasks.filter { it.status == TaskStatus.EN_PROGRESO }
            TaskFilter.COMPLETADAS -> tasks.filter { it.status == TaskStatus.COMPLETADA }
            TaskFilter.VENCIDAS -> tasks.filter { task ->
                if (task.status == TaskStatus.COMPLETADA || task.dueDate.isNullOrBlank()) {
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
