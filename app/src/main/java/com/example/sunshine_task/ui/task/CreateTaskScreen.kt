package com.example.sunshine_task.ui.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.ui.theme.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: TaskViewModel,
    onTaskCreated: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var priority by remember { mutableStateOf(Priority.MEDIA) }
    var dueDate by remember { mutableStateOf("") }

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
                            dueDate = formatter.format(Date(millis))
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Nueva tarea",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                maxLines = 3
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Text("Prioridad", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            
            val priorities = listOf(
                Priority.BAJA to ("Baja" to Color(0xFF10B981)),
                Priority.MEDIA to ("Media" to Color(0xFF3B82F6)),
                Priority.ALTA to ("Alta" to Color(0xFFF59E0B)),
                Priority.URGENTE to ("Urgente" to Color(0xFFEF4444))
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Row 1: Baja & Media
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.take(2).forEach { (p, pair) ->
                        val (label, color) = pair
                        OutlinedButton(
                            onClick = { priority = p },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (priority == p) color.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = color
                            ),
                            border = BorderStroke(1.dp, color),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(label)
                        }
                    }
                }
                // Row 2: Alta & Urgente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.drop(2).forEach { (p, pair) ->
                        val (label, color) = pair
                        OutlinedButton(
                            onClick = { priority = p },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (priority == p) color.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = color
                            ),
                            border = BorderStroke(1.dp, color),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(label)
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (dueDate.isBlank()) "Seleccionar fecha límite" else "Fecha: $dueDate")
            }

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = {
                        viewModel.addTask(title, description, category.ifBlank { "General" }, priority, dueDate)
                        if (viewModel.errorMessage == null) {
                            onTaskCreated()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
