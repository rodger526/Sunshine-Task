package com.example.sunshine_task.data.repository

import com.example.sunshine_task.data.models.Task
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a datos. La capa de presentación solo conoce esta interfaz
 * y el modelo de dominio [Task]; los detalles de Room quedan ocultos.
 */
interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    fun observeTask(taskId: String): Flow<Task?>
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
}