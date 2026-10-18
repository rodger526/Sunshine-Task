package com.example.sunshine_task.notifications

import com.example.sunshine_task.data.models.Status
import com.example.sunshine_task.data.models.Task
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

/** Calcula qué avisos faltan por programar. No usa Android, así que se puede probar con JUnit. */
object ReminderPlanner {

    data class Planned(val kind: ReminderKind, val delayMillis: Long)

    /** La fecha límite no tiene hora: se toma el final del día (23:59). */
    fun dueMoment(task: Task): LocalDateTime? =
        runCatching { LocalDate.parse(task.dueDate).atTime(23, 59) }.getOrNull()

    fun plan(task: Task, now: LocalDateTime = LocalDateTime.now()): List<Planned> {
        if (task.status == Status.COMPLETADA) return emptyList()
        val due = dueMoment(task) ?: return emptyList()

        return ReminderKind.entries.mapNotNull { kind ->
            val millis = Duration.between(now, due.minusHours(kind.hoursBefore)).toMillis()
            if (millis > 0) Planned(kind, millis) else null
        }
    }
}