package com.example.sunshine_task.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.sunshine_task.ui.components.EmptyState
import com.example.sunshine_task.ui.components.FilterSection
import com.example.sunshine_task.ui.components.SearchBar
import com.example.sunshine_task.ui.components.StatsSection
import com.example.sunshine_task.ui.components.TaskCard
import com.example.sunshine_task.ui.task.CreateTaskScreen
import com.example.sunshine_task.ui.theme.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateTask by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Sunshine Task",
                            style = MaterialTheme.typography.titleLarge
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateTask = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        ) { innerPadding ->
            val tasks = viewModel.filteredTasks

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Subtitle / header description
                item {
                    Text(
                        text = "Tus objetivos",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Statistics section
                item {
                    StatsSection(
                        total = viewModel.totalTasks,
                        completed = viewModel.completedTasks,
                        pending = viewModel.pendingTasks,
                        overdue = viewModel.overdueTasks
                    )
                }

                // Search bar
                item {
                    SearchBar(
                        query = viewModel.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) }
                    )
                }

                // Filters
                item {
                    FilterSection(
                        selectedFilter = viewModel.selectedFilter,
                        onFilterSelected = { viewModel.updateFilter(it) },
                        selectedDateFilter = viewModel.selectedDateFilter,
                        onDateFilterSelected = { viewModel.updateDateFilter(it) },
                        selectedPriorityFilter = viewModel.selectedPriorityFilter,
                        onPriorityFilterSelected = { viewModel.updatePriorityFilter(it) }
                    )
                }

                // Section Header: Mis tareas
                item {
                    Text(
                        text = "Mis tareas",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Task list or Empty state
                if (tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState()
                        }
                    }
                } else {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onTaskClick = { onTaskClick(task.id) }
                        )
                    }
                }

                // Bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Translucent overlay dialog when creating a task, blocking background clicks
        if (showCreateTask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { /* Consume clicks */ })
                    },
                contentAlignment = Alignment.Center
            ) {
                CreateTaskScreen(
                    viewModel = viewModel,
                    onTaskCreated = { showCreateTask = false },
                    onBack = { showCreateTask = false }
                )
            }
        }
    }
}
