package com.example.sunshine_task.features.search

import com.example.sunshine_task.data.models.Task

object TaskSearch {
    fun filter(tasks: List<Task>, query: String): List<Task> {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return tasks

        return tasks.filter { task ->
            task.title.contains(cleanQuery, ignoreCase = true) ||
            task.description.contains(cleanQuery, ignoreCase = true) ||
            task.category.contains(cleanQuery, ignoreCase = true)
        }
    }
}
