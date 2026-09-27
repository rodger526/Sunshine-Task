package com.example.sunshine_task.data.models

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
    val category: String,
    val priority: Priority,
    val status: Status,
    val dueDate: String
)
