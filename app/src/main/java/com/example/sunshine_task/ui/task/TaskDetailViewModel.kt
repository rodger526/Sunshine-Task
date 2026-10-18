package com.example.sunshine_task.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.repository.TaskRepository
import com.example.sunshine_task.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState
    data class Success(val task: Task) : TaskDetailUiState
    data class Error(val message: String) : TaskDetailUiState
}

sealed interface TaskDetailEvent {
    data object TaskDeleted : TaskDetailEvent
}

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository
) : ViewModel() {

    // Hilt entrega los argumentos de navegación a través de SavedStateHandle.
    private val taskId: String = checkNotNull(savedStateHandle[Routes.TASK_ID_ARG])

    val uiState: StateFlow<TaskDetailUiState> = repository.observeTask(taskId)
        .map<Task?, TaskDetailUiState> { task ->
            if (task == null) TaskDetailUiState.Error("Tarea no encontrada")
            else TaskDetailUiState.Success(task)
        }
        .catch { emit(TaskDetailUiState.Error(it.message ?: "No se pudo cargar la tarea")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskDetailUiState.Loading
        )

    private val _events = Channel<TaskDetailEvent>(Channel.BUFFERED)
    val events: Flow<TaskDetailEvent> = _events.receiveAsFlow()

    private val currentTask: Task?
        get() = (uiState.value as? TaskDetailUiState.Success)?.task

    fun toggleStatus() {
        val task = currentTask ?: return
        val newStatus = when (task.status) {
            Status.PENDIENTE -> Status.EN_PROGRESO
            Status.EN_PROGRESO -> Status.COMPLETADA
            Status.COMPLETADA -> Status.PENDIENTE
        }
        viewModelScope.launch { repository.updateTask(task.copy(status = newStatus)) }
    }

    fun updateTask(
        title: String,
        description: String,
        category: String,
        priority: Priority,
        dueDate: String
    ) {
        val task = currentTask ?: return
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return

        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    title = cleanTitle,
                    description = description,
                    category = category.ifBlank { "General" },
                    priority = priority,
                    dueDate = dueDate.ifBlank { task.dueDate }
                )
            )
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _events.send(TaskDetailEvent.TaskDeleted)
        }
    }
}