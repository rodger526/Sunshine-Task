package com.example.sunshine_task.ui.theme.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.models.TaskStatus
import com.example.sunshine_task.data.repository.TaskRepository
import java.time.LocalDateTime
import java.util.UUID

class TaskViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _tasks = mutableStateOf(repository.getTasks())
    val tasks: State<List<Task>> = _tasks

    private val _selectedTask = mutableStateOf<Task?>(null)
    val selectedTask: State<Task?> = _selectedTask

    fun selectTask(taskId: String) {
        _selectedTask.value = _tasks.value.find { it.id == taskId }
    }

    fun clearSelectedTask() {
        _selectedTask.value = null
    }

    fun addTask(
        title: String,
        description: String = "",
        category: String = "General",
        priority: Priority = Priority.MEDIUM,
        dueDate: LocalDateTime? = null
    ) {
        val cleanTitle = title.trim()

        if (cleanTitle.isEmpty()) return

        val task = Task(
            id = UUID.randomUUID().toString(),
            title = cleanTitle,
            description = description,
            category = category,
            priority = priority,
            dueDate = dueDate,
            status = TaskStatus.PENDING,
            createdAt = LocalDateTime.now()
        )

        repository.addTask(task)
        refreshTasks()
    }

    fun updateTask(task: Task) {
        repository.updateTask(task)
        refreshTasks()
        refreshSelectedTask(task.id)
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
        refreshTasks()

        if (_selectedTask.value?.id == taskId) {
            _selectedTask.value = null
        }
    }

    fun changeTaskStatus(taskId: String, newStatus: TaskStatus) {
        val task = _tasks.value.find { it.id == taskId } ?: return

        if (task.status == TaskStatus.COMPLETED) return
        if (task.status == newStatus) return

        updateTask(task.copy(status = newStatus))
    }

    private fun refreshTasks() {
        _tasks.value = repository.getTasks()
    }

    private fun refreshSelectedTask(taskId: String) {
        _selectedTask.value = _tasks.value.find { it.id == taskId }
    }
}