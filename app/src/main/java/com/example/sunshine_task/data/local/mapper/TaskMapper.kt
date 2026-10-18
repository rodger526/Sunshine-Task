package com.example.sunshine_task.data.local.mapper

import com.example.sunshine_task.data.local.entity.TaskEntity
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        category = category,
        priority = try { Priority.valueOf(priority) } catch (_: Exception) { Priority.MEDIA },
        status = try { Status.valueOf(status) } catch (_: Exception) { Status.PENDIENTE },
        dueDate = dueDate,
        createdAt = createdAt
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        category = category,
        priority = priority.name,
        status = status.name,
        dueDate = dueDate,
        createdAt = createdAt
    )
}
