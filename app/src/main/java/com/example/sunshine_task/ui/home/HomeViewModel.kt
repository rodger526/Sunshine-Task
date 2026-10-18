package com.example.sunshine_task.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.repository.TaskRepository
import com.example.sunshine_task.features.filters.TaskFilter
import com.example.sunshine_task.features.filters.TaskFilters
import com.example.sunshine_task.features.search.TaskSearch
import com.example.sunshine_task.features.statistics.TaskStatisticsCalculator
import com.example.sunshine_task.features.statistics.TaskStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: TaskFilter = TaskFilter.TODAS,
    val selectedPriority: Priority? = null,
    val selectedDate: String = "",
    val stats: TaskStats = TaskStats(0, 0, 0, 0, 0),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(TaskFilter.TODAS)
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    private val _selectedDate = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllTasks(),
        _searchQuery,
        _selectedFilter,
        _selectedPriority,
        _selectedDate
    ) { tasks, query, filter, priority, date ->
        var list = TaskFilters.filter(tasks, filter)
        list = TaskSearch.filter(list, query)
        priority?.let { p ->
            list = list.filter { it.priority == p }
        }
        if (date.isNotBlank()) {
            list = list.filter { it.dueDate == date }
        }
        val stats = TaskStatisticsCalculator.calculate(tasks)

        HomeUiState(
            tasks = tasks,
            filteredTasks = list,
            searchQuery = query,
            selectedFilter = filter,
            selectedPriority = priority,
            selectedDate = date,
            stats = stats,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun onPriorityFilterSelected(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun onDateFilterSelected(date: String) {
        _selectedDate.value = date
    }

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            val newStatus = when (task.status) {
                Status.PENDIENTE -> Status.EN_PROGRESO
                Status.EN_PROGRESO -> Status.COMPLETADA
                Status.COMPLETADA -> Status.PENDIENTE
            }
            repository.updateTask(task.copy(status = newStatus))
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}
