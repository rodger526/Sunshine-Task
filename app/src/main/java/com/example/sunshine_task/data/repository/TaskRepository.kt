package com.example.sunshine_task.data.repository

import android.content.Context
import com.example.sunshine_task.data.local.TaskStorage
import com.example.sunshine_task.data.models.Task

class TaskRepository(context: Context) {
    private val taskStorage = TaskStorage(context)
    private val tasks = taskStorage.loadTasks().toMutableList()

    fun getTasks(): List<Task> {
        return tasks.toList()
    }

    fun addTask(task: Task) {
        tasks.add(task)
        taskStorage.saveTasks(tasks)
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            taskStorage.saveTasks(tasks)
        }
    }

    fun deleteTask(taskId: String) {
        tasks.removeAll { it.id == taskId }
        taskStorage.saveTasks(tasks)
    }
}