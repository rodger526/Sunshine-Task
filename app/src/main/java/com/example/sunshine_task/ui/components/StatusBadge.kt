package com.example.sunshine_task.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.sunshine_task.data.models.Status

@Composable
fun StatusBadge(status: Status) {
    val text = when (status) {
        Status.PENDIENTE -> "Pendiente"
        Status.EN_PROGRESO -> "En progreso"
        Status.COMPLETADA -> "Completada"
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
