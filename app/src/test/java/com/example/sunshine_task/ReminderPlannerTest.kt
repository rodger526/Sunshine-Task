package com.example.sunshine_task

import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.notifications.ReminderKind
import com.example.sunshine_task.notifications.ReminderPlanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ReminderPlannerTest {

    private fun task(dueDate: String, status: Status = Status.PENDIENTE) = Task(
        id = "1",
        title = "Tarea",
        description = "",
        category = "General",
        priority = Priority.ALTA,
        status = status,
        dueDate = dueDate
    )

    @Test
    fun tareaLejana_programaLosCuatroAvisos() {
        val now = LocalDateTime.of(2026, 10, 10, 8, 0)
        val kinds = ReminderPlanner.plan(task("2026-10-20"), now).map { it.kind }
        assertEquals(listOf(ReminderKind.H24, ReminderKind.H12, ReminderKind.H1, ReminderKind.VENCIDA), kinds)
    }

    @Test
    fun tareaCercana_soloProgramaLosQueFaltan() {
        val now = LocalDateTime.of(2026, 10, 20, 20, 0)
        val kinds = ReminderPlanner.plan(task("2026-10-20"), now).map { it.kind }
        assertEquals(listOf(ReminderKind.H1, ReminderKind.VENCIDA), kinds)
    }

    @Test
    fun elAvisoDe24Horas_saleUnDiaAntesDeLas2359() {
        val now = LocalDateTime.of(2026, 10, 18, 23, 59)
        val h24 = ReminderPlanner.plan(task("2026-10-20"), now).first { it.kind == ReminderKind.H24 }
        assertEquals(24L * 60 * 60 * 1000, h24.delayMillis)
    }

    @Test
    fun tareaCompletada_noProgramaNada() {
        val now = LocalDateTime.of(2026, 10, 10, 8, 0)
        assertTrue(ReminderPlanner.plan(task("2026-10-20", Status.COMPLETADA), now).isEmpty())
    }

    @Test
    fun tareaYaVencida_noProgramaNada() {
        val now = LocalDateTime.of(2026, 10, 25, 8, 0)
        assertTrue(ReminderPlanner.plan(task("2026-10-20"), now).isEmpty())
    }

    @Test
    fun fechaInvalida_noProgramaNada() {
        assertTrue(ReminderPlanner.plan(task("sin fecha")).isEmpty())
    }
}