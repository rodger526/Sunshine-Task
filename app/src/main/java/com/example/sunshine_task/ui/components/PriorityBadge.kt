package com.example.sunshine_task.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val (text, color) = when (priority) {
        Priority.BAJA -> "Baja" to Color(0xFF10B981) // Green
        Priority.MEDIA -> "Media" to Color(0xFF3B82F6) // Blue
        Priority.ALTA -> "Alta" to Color(0xFFF59E0B) // Orange
        Priority.URGENTE -> "Urgente" to Color(0xFFEF4444) // Red
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
