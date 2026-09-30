package com.example.sunshine_task.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sunshine_task.ui.components.EmptyState
import com.example.sunshine_task.ui.components.FilterSection
import com.example.sunshine_task.ui.components.SearchBar
import com.example.sunshine_task.ui.components.StatsSection
import com.example.sunshine_task.ui.components.TaskCard
import com.example.sunshine_task.ui.theme.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sunshine Task") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subtitle / header description
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Programa tus objetivos, alcanza tu calidad de vida.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Statistics section
            StatsSection(
                total = viewModel.totalTasks,
                completed = viewModel.completedTasks,
                pending = viewModel.pendingTasks,
                inProgress = viewModel.inProgressTasks
            )

            // Search bar
            SearchBar(
                query = viewModel.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) }
            )

            // Filters
            FilterSection(
                selectedFilter = viewModel.selectedFilter,
                onFilterSelected = { viewModel.updateFilter(it) }
            )

            // Section Header: Mis tareas
            Text(
                text = "Mis tareas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Task list
            val tasks = viewModel.filteredTasks
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    EmptyState()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onTaskClick = { viewModel.toggleTaskStatus(task.id) }
                        )
                    }
                }
            }
        }
    }
}
