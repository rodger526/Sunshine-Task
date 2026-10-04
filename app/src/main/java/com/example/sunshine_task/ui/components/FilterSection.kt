package com.example.sunshine_task.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    selectedDateFilter: String,
    onDateFilterSelected: (String) -> Unit,
    selectedPriorityFilter: String,
    onPriorityFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            onDateFilterSelected(formatter.format(Date(millis)))
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        onDateFilterSelected("")
                    }
                ) {
                    Text("Limpiar fecha")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val statusFilters = listOf(
        Triple("TODAS", "Todas", Color(0xFF7C3AED)),      // Purple
        Triple("PENDIENTE", "Pendientes", Color(0xFFF59E0B)), // Yellow/Orange
        Triple("COMPLETADA", "Completadas", Color(0xFF10B981)), // Green
        Triple("VENCIDA", "Vencidas", Color(0xFFEF4444))   // Red
    )

    val priorityFilters = listOf(
        Triple("ALL", "Todas", Color.Gray),
        Triple("BAJA", "Baja", Color(0xFF10B981)),
        Triple("MEDIA", "Media", Color(0xFF3B82F6)),
        Triple("ALTA", "Alta", Color(0xFFF59E0B)),
        Triple("URGENTE", "Urgente", Color(0xFFEF4444))
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estado",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (selectedDateFilter.isBlank()) "📅 Filtrar fecha" else "📅 $selectedDateFilter",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Status filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statusFilters.forEach { (key, label, color) ->
                val isSelected = selectedFilter == key
                Surface(
                    onClick = { onFilterSelected(key) },
                    shape = MaterialTheme.shapes.small,
                    color = if (isSelected) color else Color.Transparent,
                    border = BorderStroke(1.dp, color)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else color,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Text(
            text = "Prioridad",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Priority filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            priorityFilters.forEach { (key, label, color) ->
                val isSelected = selectedPriorityFilter == key
                Surface(
                    onClick = { onPriorityFilterSelected(key) },
                    shape = MaterialTheme.shapes.small,
                    color = if (isSelected) color else Color.Transparent,
                    border = BorderStroke(1.dp, color)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else color,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
