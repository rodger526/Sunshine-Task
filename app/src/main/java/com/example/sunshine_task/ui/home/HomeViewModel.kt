package com.example.sunshine_task.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

data class HomeFilters(
    val searchQuery: String = "",
    val statusFilter: String = "TODAS",
    val dateFilter: String = "",
    val priorityFilter: String = "ALL"
)

data class HomeStats(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val overdue: Int
)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val tasks: List<Task>,
        val stats: HomeStats,
        val filters: HomeFilters
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: TaskRepository
) : ViewModel() {

    private val filters = MutableStateFlow(HomeFilters())

    /** Único flujo de estado de la pantalla: Room (Flow) + filtros -> UiState. */
    val uiState: StateFlow<HomeUiState> =
        combine<List<Task>, HomeFilters, HomeUiState>(
            repository.observeTasks(),
            filters
        ) { tasks, currentFilters ->
            val today = LocalDate.now()
            HomeUiState.Success(
                tasks = tasks.applyFilters(currentFilters, today),
                stats = tasks.toStats(today),
                filters = currentFilters
            )
        }
            .catch { emit(HomeUiState.Error(it.message ?: "No se pudieron cargar las tareas")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading
            )

    fun onSearchQueryChange(query: String) = filters.update { it.copy(searchQuery = query) }

    fun onStatusFilterChange(filter: String) = filters.update { it.copy(statusFilter = filter) }

    fun onDateFilterChange(date: String) = filters.update { it.copy(dateFilter = date) }

    fun onPriorityFilterChange(priority: String) =
        filters.update { it.copy(priorityFilter = priority) }
}

private fun Task.isOverdue(today: LocalDate): Boolean =
    status != Status.COMPLETADA &&
        dueDate.isNotBlank() &&
        runCatching { LocalDate.parse(dueDate).isBefore(today) }.getOrDefault(false)

private fun List<Task>.toStats(today: LocalDate) = HomeStats(
    total = size,
    completed = count { it.status == Status.COMPLETADA },
    pending = count { it.status == Status.PENDIENTE || it.status == Status.EN_PROGRESO },
    overdue = count { it.isOverdue(today) }
)

private fun List<Task>.applyFilters(filters: HomeFilters, today: LocalDate): List<Task> =
    filter { task ->
        val matchesSearch =
            task.title.contains(filters.searchQuery, ignoreCase = true) ||
                task.description.contains(filters.searchQuery, ignoreCase = true)

        val matchesStatus = when (filters.statusFilter) {
            "PENDIENTE" -> task.status == Status.PENDIENTE || task.status == Status.EN_PROGRESO
            "COMPLETADA" -> task.status == Status.COMPLETADA
            "VENCIDA" -> task.isOverdue(today)
            else -> true
        }

        val matchesDate = filters.dateFilter.isBlank() || task.dueDate == filters.dateFilter

        val matchesPriority = when (filters.priorityFilter) {
            "BAJA" -> task.priority == Priority.BAJA
            "MEDIA" -> task.priority == Priority.MEDIA
            "ALTA" -> task.priority == Priority.ALTA
            "URGENTE" -> task.priority == Priority.URGENTE
            else -> true
        }

        matchesSearch && matchesStatus && matchesDate && matchesPriority
    }