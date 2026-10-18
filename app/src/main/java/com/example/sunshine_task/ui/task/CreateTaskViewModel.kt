package com.example.sunshine_task.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val priority: Priority = Priority.MEDIA,
    val dueDate: String? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false
)

sealed interface CreateTaskEvent {
    object TaskCreated : CreateTaskEvent
    data class ShowError(val message: String) : CreateTaskEvent
}

class CreateTaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateTaskEvent>()
    val events = _events.asSharedFlow()

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, errorMessage = null) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onCategoryChanged(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onPriorityChanged(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onDueDateChanged(date: String?) {
        _uiState.update { it.copy(dueDate = date) }
    }

    fun saveTask() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El título es requerido") }
            viewModelScope.launch { _events.emit(CreateTaskEvent.ShowError("El título es requerido")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val task = Task(
                title = state.title.trim(),
                description = state.description.trim(),
                category = state.category.ifBlank { "General" },
                priority = state.priority,
                dueDate = state.dueDate
            )
            repository.insertTask(task)
            _events.emit(CreateTaskEvent.TaskCreated)
        }
    }
}
