package com.example.sunshine_task.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.models.TaskAttachment
import com.example.sunshine_task.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val task: Task? = null,
    val attachments: List<TaskAttachment> = emptyList(),
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val editTitle: String = "",
    val editDescription: String = "",
    val editCategory: String = "",
    val editPriority: Priority = Priority.MEDIA,
    val editDueDate: String? = null
)

sealed interface TaskDetailEvent {
    object TaskDeleted : TaskDetailEvent
    object TaskUpdated : TaskDetailEvent
}

class TaskDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TaskDetailEvent>()
    val events = _events.asSharedFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            repository.getTaskById(taskId).collect { task ->
                _uiState.update { current ->
                    current.copy(
                        task = task,
                        editTitle = task?.title ?: "",
                        editDescription = task?.description ?: "",
                        editCategory = task?.category ?: "",
                        editPriority = task?.priority ?: Priority.MEDIA,
                        editDueDate = task?.dueDate,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onStatusChanged(newStatus: Status) {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            repository.updateTask(currentTask.copy(status = newStatus))
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _events.emit(TaskDetailEvent.TaskDeleted)
        }
    }

    fun addAttachment(attachment: TaskAttachment) {
        _uiState.update { current ->
            if (current.attachments.any { it.uri == attachment.uri }) current
            else current.copy(attachments = current.attachments + attachment)
        }
    }

    fun removeAttachment(uri: String) {
        _uiState.update { current ->
            current.copy(attachments = current.attachments.filterNot { it.uri == uri })
        }
    }
}
