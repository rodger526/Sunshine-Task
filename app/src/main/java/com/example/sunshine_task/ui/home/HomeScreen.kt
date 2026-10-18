package com.example.sunshine_task.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sunshine_task.ui.components.EmptyState
import com.example.sunshine_task.ui.components.FilterSection
import com.example.sunshine_task.ui.components.SearchBar
import com.example.sunshine_task.ui.components.StatsSection
import com.example.sunshine_task.ui.components.TaskCard

/**
 * Composable con estado (Stateful / Route): es el único que conoce el ViewModel.
 * Consume el StateFlow de forma segura con el ciclo de vida.
 */
@Composable
fun HomeRoute(
    onTaskClick: (String) -> Unit,
    onCreateTaskClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onDateFilterChange = viewModel::onDateFilterChange,
        onPriorityFilterChange = viewModel::onPriorityFilterChange,
        onTaskClick = onTaskClick,
        onCreateTaskClick = onCreateTaskClick,
        modifier = modifier
    )
}

/** Vista sin estado (Stateless / Content): solo recibe estado inmutable y lambdas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onDateFilterChange: (String) -> Unit,
    onPriorityFilterChange: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onCreateTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                onClick = onCreateTaskClick,
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
        when (uiState) {
            HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Tus objetivos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        StatsSection(
                            total = uiState.stats.total,
                            completed = uiState.stats.completed,
                            pending = uiState.stats.pending,
                            overdue = uiState.stats.overdue
                        )
                    }

                    item {
                        SearchBar(
                            query = uiState.filters.searchQuery,
                            onQueryChange = onSearchQueryChange
                        )
                    }

                    item {
                        FilterSection(
                            selectedFilter = uiState.filters.statusFilter,
                            onFilterSelected = onStatusFilterChange,
                            selectedDateFilter = uiState.filters.dateFilter,
                            onDateFilterSelected = onDateFilterChange,
                            selectedPriorityFilter = uiState.filters.priorityFilter,
                            onPriorityFilterSelected = onPriorityFilterChange
                        )
                    }

                    item {
                        Text(
                            text = "Mis tareas",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (uiState.tasks.isEmpty()) {
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
                        items(uiState.tasks, key = { it.id }) { task ->
                            TaskCard(
                                task = task,
                                onTaskClick = { onTaskClick(task.id) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}