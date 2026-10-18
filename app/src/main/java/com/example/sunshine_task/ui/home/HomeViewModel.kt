package com.example.sunshine_task.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.repository.TaskRepository
import com.example.sunshine_task.features.filters.TaskFilter
import com.example.sunshine_task.features.filters.TaskFilters
import com.example.sunshine_task.features.search.TaskSearch
import com.example.sunshine_task.features.statistics.TaskStatisticsCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
            val stats = TaskStatisticsCalculator.calculate(tasks)
            HomeUiState.Success(
                tasks = applyFilters(tasks, currentFilters),
                stats = HomeStats(
                    total = stats.total,
                    completed = stats.completadas,
                    pending = stats.pendientes + stats.enProgreso,
                    overdue = stats.vencidas
                ),
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

/** Búsqueda, estado y vencidas se delegan en features/; fecha y prioridad se aplican aquí. */
private fun applyFilters(tasks: List<Task>, filters: HomeFilters): List<Task> {
    val searched = TaskSearch.filter(tasks, filters.searchQuery)

    val byStatus = when (filters.statusFilter) {
        "PENDIENTE" -> {
            val open = TaskFilters.filter(searched, TaskFilter.PENDIENTES) +
                TaskFilters.filter(searched, TaskFilter.EN_PROGRESO)
            searched.filter { it in open }
        }
        "COMPLETADA" -> TaskFilters.filter(searched, TaskFilter.COMPLETADAS)
        "VENCIDA" -> TaskFilters.filter(searched, TaskFilter.VENCIDAS)
        else -> searched
    }

    return byStatus.filter { task ->
        val matchesDate = filters.dateFilter.isBlank() || task.dueDate == filters.dateFilter

        val matchesPriority = when (filters.priorityFilter) {
            "BAJA" -> task.priority == Priority.BAJA
            "MEDIA" -> task.priority == Priority.MEDIA
            "ALTA" -> task.priority == Priority.ALTA
            "URGENTE" -> task.priority == Priority.URGENTE
            else -> true
        }

        matchesDate && matchesPriority
    }
}