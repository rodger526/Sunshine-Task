package com.example.sunshine_task.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val status: String,
    val dueDate: String?,
    val createdAt: Long
)
