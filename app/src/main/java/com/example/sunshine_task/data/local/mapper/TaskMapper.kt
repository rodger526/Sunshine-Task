package com.example.sunshine_task.data.local.mapper

import com.example.sunshine_task.data.local.entity.TaskEntity
import com.example.sunshine_task.data.models.Task
import java.time.LocalDateTime

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    status = status,
    dueDate = dueDate,
    createdAt = runCatching { LocalDateTime.parse(createdAt) }.getOrDefault(LocalDateTime.now())
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    status = status,
    dueDate = dueDate,
    createdAt = createdAt.toString()
)