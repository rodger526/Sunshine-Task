package com.example.sunshine_task.data.repository

import com.example.sunshine_task.data.models.Task

class TaskRepository {

    private val tasks = mutableListOf<Task>()

    fun getTasks(): List<Task> {
        return tasks.toList()
    }

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }

        if (index != -1) {
            tasks[index] = task
        }
    }

    fun deleteTask(taskId: String) {
        tasks.removeAll { it.id == taskId }
    }
}
