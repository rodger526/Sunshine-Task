package com.example.sunshine_task.data.models

import java.time.LocalDateTime

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: Priority,
    val dueDate: LocalDateTime?,
    val status: TaskStatus,
    val createdAt: LocalDateTime
)