
package com.example.sunshine_task.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.repository.TaskRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class TaskViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf("TODAS")
        private set

    var selectedDateFilter by mutableStateOf("")
        private set

    var selectedPriorityFilter by mutableStateOf("ALL")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var tasks by mutableStateOf(repository.getTasks())
        private set

    var selectedTask by mutableStateOf<Task?>(null)
        private set

    init {
        if (repository.getTasks().isEmpty()) {
            repository.addTask(
                Task(
                    id = "1",
                    title = "Estudiar Jetpack Compose",
                    description = "Revisar componentes Material 3 y layouts avanzados.",
                    category = "Estudio",
                    priority = Priority.ALTA,
                    status = Status.EN_PROGRESO,
                    dueDate = "2026-10-20"
                )
            )

            repository.addTask(
                Task(
                    id = "2",
                    title = "Hacer ejercicio matutino",
                    description = "Rutina de cardio y estiramientos por 30 minutos.",
                    category = "Salud",
                    priority = Priority.MEDIA,
                    status = Status.COMPLETADA,
                    dueDate = "2026-10-18"
                )
            )

            repository.addTask(
                Task(
                    id = "3",
                    title = "Revisar arquitectura del proyecto",
                    description = "Revisar la organización de las capas.",
                    category = "Trabajo",
                    priority = Priority.URGENTE,
                    status = Status.PENDIENTE,
                    dueDate = "2026-10-14"
                )
            )

            repository.addTask(
                Task(
                    id = "4",
                    title = "Comprar víveres",
                    description = "Frutas, verduras, leche y café.",
                    category = "Personal",
                    priority = Priority.BAJA,
                    status = Status.PENDIENTE,
                    dueDate = "2026-10-25"
                )
            )

            refreshTasks()
        }
    }

    val totalTasks: Int
        get() = tasks.size

    val completedTasks: Int
        get() = tasks.count { it.status == Status.COMPLETADA }

    val pendingTasks: Int
        get() = tasks.count {
            it.status == Status.PENDIENTE ||
                it.status == Status.EN_PROGRESO
        }

    val inProgressTasks: Int
        get() = tasks.count { it.status == Status.EN_PROGRESO }

    val overdueTasks: Int
        get() = tasks.count {
            it.status != Status.COMPLETADA &&
                it.dueDate.isNotBlank() &&
                runCatching {
                    LocalDate.parse(it.dueDate).isBefore(LocalDate.now())
                }.getOrDefault(false)
        }

    val filteredTasks: List<Task>
        get() = tasks.filter { task ->
            val matchesSearch =
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true)

            val isOverdue = task.status != Status.COMPLETADA &&
                task.dueDate.isNotBlank() &&
                runCatching {
                    LocalDate.parse(task.dueDate).isBefore(LocalDate.now())
                }.getOrDefault(false)

            val matchesFilter = when (selectedFilter) {
                "PENDIENTE" ->
                    task.status == Status.PENDIENTE ||
                        task.status == Status.EN_PROGRESO

                "COMPLETADA" -> task.status == Status.COMPLETADA
                "VENCIDA" -> isOverdue
                else -> true
            }

            val matchesDate =
                selectedDateFilter.isBlank() ||
                    task.dueDate == selectedDateFilter

            val matchesPriority = when (selectedPriorityFilter) {
                "BAJA" -> task.priority == Priority.BAJA
                "MEDIA" -> task.priority == Priority.MEDIA
                "ALTA" -> task.priority == Priority.ALTA
                "URGENTE" -> task.priority == Priority.URGENTE
                else -> true
            }

            matchesSearch &&
                matchesFilter &&
                matchesDate &&
                matchesPriority
        }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun updateFilter(filter: String) {
        selectedFilter = filter
    }

    fun updateDateFilter(date: String) {
        selectedDateFilter = date
    }

    fun updatePriorityFilter(priority: String) {
        selectedPriorityFilter = priority
    }

    fun selectTask(taskId: String) {
        selectedTask = tasks.find { it.id == taskId }
    }

    fun clearSelectedTask() {
        selectedTask = null
    }

    fun addTask(
        title: String,
        description: String = "",
        category: String = "General",
        priority: Priority = Priority.MEDIA,
        dueDate: String = "2026-12-31"
    ) {
        val cleanTitle = title.trim()

        if (cleanTitle.isEmpty()) {
            errorMessage = "Título requerido"
            return
        }

        errorMessage = null

        val task = Task(
            id = UUID.randomUUID().toString(),
            title = cleanTitle,
            description = description,
            category = category.ifBlank { "General" },
            priority = priority,
            status = Status.PENDIENTE,
            dueDate = dueDate.ifBlank { "2026-12-31" },
            createdAt = LocalDateTime.now()
        )

        repository.addTask(task)
        refreshTasks()
    }

    fun updateTask(
        taskId: String,
        title: String,
        description: String,
        category: String,
        priority: Priority,
        dueDate: String
    ) {
        val cleanTitle = title.trim()

        if (cleanTitle.isEmpty()) {
            errorMessage = "Título requerido"
            return
        }

        val existingTask = tasks.find { it.id == taskId } ?: return

        updateTask(
            existingTask.copy(
                title = cleanTitle,
                description = description,
                category = category.ifBlank { "General" },
                priority = priority,
                dueDate = dueDate.ifBlank { existingTask.dueDate }
            )
        )
    }

    fun updateTask(task: Task) {
        val cleanTitle = task.title.trim()

        if (cleanTitle.isEmpty()) {
            errorMessage = "Título requerido"
            return
        }

        val existingTask = tasks.find { it.id == task.id } ?: return

        errorMessage = null

        val updatedTask = existingTask.copy(
            title = cleanTitle,
            description = task.description,
            category = task.category.ifBlank { "General" },
            priority = task.priority,
            dueDate = task.dueDate,
            status = task.status
        )

        repository.updateTask(updatedTask)
        refreshTasks()
        refreshSelectedTask(updatedTask.id)
    }

    fun toggleTaskStatus(taskId: String) {
        val task = tasks.find { it.id == taskId } ?: return

        val newStatus = when (task.status) {
            Status.PENDIENTE -> Status.EN_PROGRESO
            Status.EN_PROGRESO -> Status.COMPLETADA
            Status.COMPLETADA -> Status.PENDIENTE
        }

        updateTask(task.copy(status = newStatus))
    }

    fun changeTaskStatus(taskId: String, newStatus: Status) {
        val task = tasks.find { it.id == taskId } ?: return
        updateTask(task.copy(status = newStatus))
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
        refreshTasks()

        if (selectedTask?.id == taskId) {
            selectedTask = null
        }
    }

    private fun refreshTasks() {
        tasks = repository.getTasks()
    }

    private fun refreshSelectedTask(taskId: String) {
        selectedTask = tasks.find { it.id == taskId }
    }
}
