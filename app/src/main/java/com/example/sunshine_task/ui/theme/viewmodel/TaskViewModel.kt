package com.example.sunshine_task.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Success(
        val tasks: List<Task>,
        val filteredTasks: List<Task>,
        val totalTasks: Int,
        val completedTasks: Int,
        val pendingTasks: Int,
        val inProgressTasks: Int,
        val overdueTasks: Int,
        val searchQuery: String = "",
        val selectedFilter: String = "TODAS",
        val selectedDateFilter: String = "",
        val selectedPriorityFilter: String = "ALL",
        val errorMessage: String? = null
    ) : TaskUiState
    data class Error(val message: String) : TaskUiState
}

class TaskViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    val tasks: List<Task>
        get() = (uiState.value as? TaskUiState.Success)?.tasks ?: emptyList()

    val errorMessage: String?
        get() = (uiState.value as? TaskUiState.Success)?.errorMessage

    private var searchQuery = ""
    private var selectedFilter = "TODAS"
    private var selectedDateFilter = ""
    private var selectedPriorityFilter = "ALL"
    private var errorMessageInternal: String? = null

    private var tasksList = listOf(
        Task(
            id = "1",
            title = "Estudiar Jetpack Compose",
            description = "Revisar componentes Material 3 y layouts avanzados.",
            category = "Estudio",
            priority = Priority.ALTA,
            status = Status.EN_PROGRESO,
            dueDate = "2026-10-01"
        ),
        Task(
            id = "2",
            title = "Hacer ejercicio matutino",
            description = "Rutina de cardio y estiramientos por 30 minutos.",
            category = "Salud",
            priority = Priority.MEDIA,
            status = Status.COMPLETADA,
            dueDate = "2026-09-28"
        ),
        Task(
            id = "3",
            title = "Revisar arquitectura del proyecto",
            description = "Asegurar separación de capas UI, Core y modelos.",
            category = "Trabajo",
            priority = Priority.URGENTE,
            status = Status.PENDIENTE,
            dueDate = "2026-09-29"
        ),
        Task(
            id = "4",
            title = "Comprar víveres",
            description = "Frutas, verduras, leche y café.",
            category = "Personal",
            priority = Priority.BAJA,
            status = Status.PENDIENTE,
            dueDate = "2026-09-30"
        )
    )

    init {
        updateState()
    }

    private fun updateState() {
        val total = tasksList.size
        val completed = tasksList.count { it.status == Status.COMPLETADA }
        val pending = tasksList.count { it.status == Status.PENDIENTE || it.status == Status.EN_PROGRESO }
        val inProgress = tasksList.count { it.status == Status.EN_PROGRESO }
        val overdue = tasksList.count { it.status != Status.COMPLETADA && it.dueDate < "2026-10-01" }

        val filtered = tasksList.filter { task ->
            val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true)
            
            val isOverdue = task.status != Status.COMPLETADA && task.dueDate < "2026-10-01"

            val matchesFilter = when (selectedFilter) {
                "PENDIENTE" -> task.status == Status.PENDIENTE || task.status == Status.EN_PROGRESO
                "COMPLETADA" -> task.status == Status.COMPLETADA
                "VENCIDA" -> isOverdue
                else -> true
            }

            val matchesDate = if (selectedDateFilter.isBlank()) {
                true
            } else {
                task.dueDate == selectedDateFilter
            }

            val matchesPriority = when (selectedPriorityFilter) {
                "BAJA" -> task.priority == Priority.BAJA
                "MEDIA" -> task.priority == Priority.MEDIA
                "ALTA" -> task.priority == Priority.ALTA
                "URGENTE" -> task.priority == Priority.URGENTE
                else -> true
            }

            matchesSearch && matchesFilter && matchesDate && matchesPriority
        }

        _uiState.value = TaskUiState.Success(
            tasks = tasksList,
            filteredTasks = filtered,
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            inProgressTasks = inProgress,
            overdueTasks = overdue,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
            selectedDateFilter = selectedDateFilter,
            selectedPriorityFilter = selectedPriorityFilter,
            errorMessage = errorMessageInternal
        )
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        updateState()
    }

    fun updateFilter(filter: String) {
        selectedFilter = filter
        updateState()
    }

    fun updateDateFilter(date: String) {
        selectedDateFilter = date
        updateState()
    }

    fun updatePriorityFilter(priority: String) {
        selectedPriorityFilter = priority
        updateState()
    }

    fun toggleTaskStatus(taskId: String) {
        tasksList = tasksList.map { task ->
            if (task.id == taskId) {
                val newStatus = when (task.status) {
                    Status.PENDIENTE -> Status.EN_PROGRESO
                    Status.EN_PROGRESO -> Status.COMPLETADA
                    Status.COMPLETADA -> Status.PENDIENTE
                }
                task.copy(status = newStatus)
            } else {
                task
            }
        }
        updateState()
    }

    fun addTask(title: String, description: String, category: String, priority: Priority, dueDate: String) {
        if (title.isBlank()) {
            errorMessageInternal = "Título requerido"
            updateState()
            return
        }
        errorMessageInternal = null
        val newTask = Task(
            id = (tasksList.size + 1).toString(),
            title = title,
            description = description,
            category = category.ifBlank { "General" },
            priority = priority,
            status = Status.PENDIENTE,
            dueDate = dueDate.ifBlank { "2026-12-31" }
        )
        tasksList = tasksList + newTask
        updateState()
    }

    fun deleteTask(taskId: String) {
        tasksList = tasksList.filter { it.id != taskId }
        updateState()
    }

    fun updateTask(taskId: String, title: String, description: String, category: String, priority: Priority, dueDate: String) {
        if (title.isBlank()) {
            errorMessageInternal = "Título requerido"
            updateState()
            return
        }
        errorMessageInternal = null
        tasksList = tasksList.map { task ->
            if (task.id == taskId) {
                task.copy(
                    title = title,
                    description = description,
                    category = category.ifBlank { "General" },
                    priority = priority,
                    dueDate = dueDate.ifBlank { task.dueDate }
                )
            } else {
                task
            }
        }
        updateState()
    }
}
