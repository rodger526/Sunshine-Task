package com.example.sunshine_task.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        "TODAS" to "Todas",
        "PENDIENTE" to "Pendientes",
        "EN_PROGRESO" to "En Proceso",
        "COMPLETADA" to "Completadas"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (key, label) ->
            FilterChip(
                selected = selectedFilter == key,
                onClick = { onFilterSelected(key) },
                label = { Text(label) }
            )
        }
    }
}
