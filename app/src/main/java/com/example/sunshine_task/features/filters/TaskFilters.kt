package com.example.sunshine_task.features.filters

import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import java.time.LocalDate

enum class TaskFilter {
    TODAS,
    PENDIENTES,
    EN_PROGRESO,
    COMPLETADAS,
    VENCIDAS
}

/** Vencida: no está completada y su fecha límite ya pasó (el día de vencimiento no cuenta). */
fun Task.isOverdue(today: LocalDate = LocalDate.now()): Boolean =
    status != Status.COMPLETADA &&
        dueDate.isNotBlank() &&
        runCatching { LocalDate.parse(dueDate).isBefore(today) }.getOrDefault(false)

object TaskFilters {

    fun filter(tasks: List<Task>, filter: TaskFilter): List<Task> {
        val today = LocalDate.now()

        return when (filter) {
            TaskFilter.TODAS -> tasks
            TaskFilter.PENDIENTES -> tasks.filter { it.status == Status.PENDIENTE }
            TaskFilter.EN_PROGRESO -> tasks.filter { it.status == Status.EN_PROGRESO }
            TaskFilter.COMPLETADAS -> tasks.filter { it.status == Status.COMPLETADA }
            TaskFilter.VENCIDAS -> tasks.filter { it.isOverdue(today) }
        }
    }
}