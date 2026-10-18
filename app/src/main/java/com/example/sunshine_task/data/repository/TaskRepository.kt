package com.example.sunshine_task.data.repository

import com.example.sunshine_task.data.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
}
