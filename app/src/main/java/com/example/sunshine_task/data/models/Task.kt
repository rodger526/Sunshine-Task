package com.example.sunshine_task.data.models

import java.time.LocalDateTime

enum class Priority {
    BAJA, MEDIA, ALTA, URGENTE
}

enum class Status {
    PENDIENTE, EN_PROGRESO, COMPLETADA
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: String = "General",
    val priority: Priority = Priority.MEDIA,
    val status: Status = Status.PENDIENTE,
    val dueDate: String = "2026-12-31",
    val createdAt: LocalDateTime = LocalDateTime.now()
)