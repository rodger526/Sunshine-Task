package com.example.sunshine_task.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

sealed interface CreateTaskUiState {
    data object Idle : CreateTaskUiState
    data object Saving : CreateTaskUiState
    data class Error(val message: String) : CreateTaskUiState
}

/** Eventos puntuales (one-off): se consumen una sola vez, no forman parte del estado. */
sealed interface CreateTaskEvent {
    data object TaskCreated : CreateTaskEvent
}

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateTaskUiState>(CreateTaskUiState.Idle)
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreateTaskEvent>(Channel.BUFFERED)
    val events: Flow<CreateTaskEvent> = _events.receiveAsFlow()

    fun createTask(
        title: String,
        description: String,
        category: String,
        priority: Priority,
        dueDate: String
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) {
            _uiState.value = CreateTaskUiState.Error("Título requerido")
            return
        }

        _uiState.value = CreateTaskUiState.Saving

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

        viewModelScope.launch {
            try {
                repository.addTask(task)
                _uiState.value = CreateTaskUiState.Idle
                _events.send(CreateTaskEvent.TaskCreated)
            } catch (e: Exception) {
                _uiState.value = CreateTaskUiState.Error(
                    e.message ?: "No se pudo guardar la tarea"
                )
            }
        }
    }

    fun clearError() {
        if (_uiState.value is CreateTaskUiState.Error) {
            _uiState.value = CreateTaskUiState.Idle
        }
    }
}