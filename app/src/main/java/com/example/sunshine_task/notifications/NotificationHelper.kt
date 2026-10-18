package com.example.sunshine_task.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.sunshine_task.MainActivity
import com.example.sunshine_task.data.models.Priority

object NotificationHelper {

    const val CHANNEL_ID = "recordatorios_tareas"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios de tareas",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos cuando una tarea está por vencer o ya venció"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun show(context: Context, taskId: String, title: String, priority: Priority, kind: ReminderKind) {
        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val heading = if (kind == ReminderKind.VENCIDA) "Tarea vencida: $title" else "Tarea por vencer: $title"
        val text = when (kind) {
            ReminderKind.H24 -> "Prioridad ${priority.label()}. Vence en 24 horas."
            ReminderKind.H12 -> "Prioridad ${priority.label()}. Vence en 12 horas."
            ReminderKind.H1 -> "Prioridad ${priority.label()}. Vence en 1 hora."
            ReminderKind.VENCIDA -> "Prioridad ${priority.label()}. La tarea ha vencido y no se ha completado."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(heading)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify("$taskId-${kind.name}".hashCode(), notification)
    }

    private fun Priority.label(): String = when (this) {
        Priority.BAJA -> "Baja"
        Priority.MEDIA -> "Media"
        Priority.ALTA -> "Alta"
        Priority.URGENTE -> "Urgente"
    }
}