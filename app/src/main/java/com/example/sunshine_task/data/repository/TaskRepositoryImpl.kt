package com.example.sunshine_task.data.repository

import com.example.sunshine_task.data.local.dao.TaskDao
import com.example.sunshine_task.data.local.mapper.toDomain
import com.example.sunshine_task.data.local.mapper.toEntity
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Implementación con Room como única fuente de verdad (offline-first). */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val reminderScheduler: ReminderScheduler
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> =
        taskDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeTask(taskId: String): Flow<Task?> =
        taskDao.observeById(taskId).map { it?.toDomain() }

    override suspend fun addTask(task: Task) {
        taskDao.insert(task.toEntity())
        reminderScheduler.schedule(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
        reminderScheduler.schedule(task)
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(taskId)
        reminderScheduler.cancel(taskId)
    }
}