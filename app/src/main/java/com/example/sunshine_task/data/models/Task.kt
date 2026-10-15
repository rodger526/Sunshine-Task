package com.example.sunshine_task.data.models

import java.time.LocalDateTime

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: Priority,
    val status: Status = Status.PENDIENTE,
    val dueDate: String = "2026-12-31",
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class Priority {
    BAJA, MEDIA, ALTA, URGENTE
}

enum class Status {
    PENDIENTE, EN_PROGRESO, COMPLETADA
}
