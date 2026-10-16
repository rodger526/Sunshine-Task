package com.example.sunshine_task.ui.theme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sunshine_task.data.local.TaskStorage
import com.example.sunshine_task.features.filters.TaskFilter
import com.example.sunshine_task.features.filters.TaskFilters
import com.example.sunshine_task.features.search.TaskSearch
import com.example.sunshine_task.features.statistics.TaskStatisticsCalculator
import com.example.sunshine_task.features.statistics.TaskStats
import com.example.sunshine_task.model.Task
import com.example.sunshine_task.model.TaskPriority
import com.example.sunshine_task.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Success(
        val tasks: List<Task>,
        val filteredTasks: List<Task>,
        val stats: TaskStats,
        val searchQuery: String = "",
        val selectedFilter: TaskFilter = TaskFilter.TODAS,
        val errorMessage: String? = null
    ) : TaskUiState
    data class Error(val message: String) : TaskUiState
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskStorage = TaskStorage(application)

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var searchQuery = ""
    private var selectedFilter = TaskFilter.TODAS
    private var errorMessage: String? = null

    private var tasks = listOf(
        Task(
            id = "1",
            title = "Estudiar Jetpack Compose",
            description = "Revisar componentes Material 3 y layouts avanzados.",
            category = "Estudio",
            priority = TaskPriority.ALTA,
            status = TaskStatus.EN_PROGRESO,
            dueDate = "2026-10-01"
        )
    )

    init {
        val loaded = taskStorage.loadTasks()
        if (loaded.isNotEmpty()) {
            tasks = loaded
        } else {
            taskStorage.saveTasks(tasks)
        }
        updateState()
    }

    private fun updateState() {
        val searched = TaskSearch.filter(tasks, searchQuery)
        val filtered = TaskFilters.filter(searched, selectedFilter)
        val stats = TaskStatisticsCalculator.calculate(tasks)

        _uiState.value = TaskUiState.Success(
            tasks = tasks,
            filteredTasks = filtered,
            stats = stats,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
            errorMessage = errorMessage
        )
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        updateState()
    }

    fun updateFilter(filter: TaskFilter) {
        selectedFilter = filter
        updateState()
    }

    fun addTask(title: String, description: String, category: String, priority: TaskPriority, dueDate: String) {
        if (title.isBlank()) {
            errorMessage = "Título requerido"
            updateState()
            return
        }
        errorMessage = null
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            category = category.ifBlank { "General" },
            priority = priority,
            status = TaskStatus.PENDIENTE,
            dueDate = dueDate.ifBlank { "2026-12-31" }
        )
        tasks = tasks + newTask
        taskStorage.saveTasks(tasks)
        updateState()
    }

    fun deleteTask(taskId: String) {
        tasks = tasks.filter { it.id != taskId }
        taskStorage.deleteTask(taskId)
        updateState()
    }

    fun toggleTaskStatus(taskId: String) {
        tasks = tasks.map { task ->
            if (task.id == taskId) {
                val newStatus = when (task.status) {
                    TaskStatus.PENDIENTE -> TaskStatus.EN_PROGRESO
                    TaskStatus.EN_PROGRESO -> TaskStatus.COMPLETADA
                    TaskStatus.COMPLETADA -> TaskStatus.PENDIENTE
                }
                task.copy(status = newStatus)
            } else {
                task
            }
        }
        taskStorage.saveTasks(tasks)
        updateState()
    }
}
