package com.example.sunshine_task.model

enum class TaskPriority {
    BAJA, MEDIA, ALTA, URGENTE
}

enum class TaskStatus {
    PENDIENTE, EN_PROGRESO, COMPLETADA
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: String = "General",
    val priority: TaskPriority = TaskPriority.MEDIA,
    val status: TaskStatus = TaskStatus.PENDIENTE,
    val dueDate: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
