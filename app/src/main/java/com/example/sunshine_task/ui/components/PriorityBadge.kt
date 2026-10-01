package com.example.sunshine_task.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sunshine_task.data.models.Priority

@Composable
fun CategoryBadge(category: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val (text, containerColor) = when (priority) {
        Priority.BAJA -> "Baja" to MaterialTheme.colorScheme.surfaceVariant
        Priority.MEDIA -> "Media" to MaterialTheme.colorScheme.secondaryContainer
        Priority.ALTA -> "Alta" to MaterialTheme.colorScheme.tertiaryContainer
        Priority.URGENTE -> "Urgente" to MaterialTheme.colorScheme.errorContainer
    }
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
