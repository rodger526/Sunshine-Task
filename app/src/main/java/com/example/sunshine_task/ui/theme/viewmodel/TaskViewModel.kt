package com.example.sunshine_task.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task

class TaskViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf("TODAS")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var tasks by mutableStateOf(
        listOf(
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
    )
        private set

    val totalTasks: Int get() = tasks.size
    val completedTasks: Int get() = tasks.count { it.status == Status.COMPLETADA }
    val pendingTasks: Int get() = tasks.count { it.status == Status.PENDIENTE }
    val inProgressTasks: Int get() = tasks.count { it.status == Status.EN_PROGRESO }

    val filteredTasks: List<Task>
        get() {
            return tasks.filter { task ->
                val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) ||
                        task.description.contains(searchQuery, ignoreCase = true)
                val matchesFilter = when (selectedFilter) {
                    "PENDIENTE" -> task.status == Status.PENDIENTE
                    "EN_PROGRESO" -> task.status == Status.EN_PROGRESO
                    "COMPLETADA" -> task.status == Status.COMPLETADA
                    else -> true
                }
                matchesSearch && matchesFilter
            }
        }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun updateFilter(filter: String) {
        selectedFilter = filter
    }

    fun toggleTaskStatus(taskId: String) {
        tasks = tasks.map { task ->
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
    }

    fun addTask(title: String, description: String, category: String, priority: Priority, dueDate: String) {
        if (title.isBlank()) {
            errorMessage = "El título no puede estar vacío (Validación desde ViewModel)"
            return
        }
        errorMessage = null
        val newTask = Task(
            id = (tasks.size + 1).toString(),
            title = title,
            description = description,
            category = category.ifBlank { "General" },
            priority = priority,
            status = Status.PENDIENTE,
            dueDate = dueDate.ifBlank { "2026-12-31" }
        )
        tasks = tasks + newTask
    }
}
