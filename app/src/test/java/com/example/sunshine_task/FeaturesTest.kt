package com.example.sunshine_task

import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.features.search.TaskSearch
import com.example.sunshine_task.features.statistics.TaskStatisticsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class FeaturesTest {

    private fun task(id: String, title: String, category: String = "General", status: Status = Status.PENDIENTE) =
        Task(
            id = id,
            title = title,
            description = "",
            category = category,
            priority = Priority.MEDIA,
            status = status
        )

    @Test
    fun search_matchesTitle() {
        val tasks = listOf(task("1", "Comprar pan"), task("2", "Estudiar"))
        assertEquals(listOf("1"), TaskSearch.filter(tasks, "pan").map { it.id })
    }

    @Test
    fun search_matchesCategory() {
        val tasks = listOf(task("1", "A", category = "Universidad"), task("2", "B", category = "Casa"))
        assertEquals(listOf("1"), TaskSearch.filter(tasks, "univ").map { it.id })
    }

    @Test
    fun search_blankQueryReturnsAll() {
        val tasks = listOf(task("1", "A"), task("2", "B"))
        assertEquals(2, TaskSearch.filter(tasks, "  ").size)
    }

    @Test
    fun statistics_countsByStatus() {
        val tasks = listOf(
            task("1", "A", status = Status.PENDIENTE),
            task("2", "B", status = Status.EN_PROGRESO),
            task("3", "C", status = Status.COMPLETADA)
        )
        val stats = TaskStatisticsCalculator.calculate(tasks)
        assertEquals(3, stats.total)
        assertEquals(1, stats.pendientes)
        assertEquals(1, stats.enProgreso)
        assertEquals(1, stats.completadas)
    }
}