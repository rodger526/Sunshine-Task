package com.example.sunshine_task.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: Priority,
    val status: Status,
    val dueDate: String
)
