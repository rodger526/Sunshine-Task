package com.example.sunshine_task.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sunshine_task.data.models.Priority

/** Se ejecuta a la hora del aviso y muestra la notificación. */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val priority = runCatching { Priority.valueOf(inputData.getString(KEY_PRIORITY) ?: "") }
            .getOrDefault(Priority.MEDIA)
        val kind = runCatching { ReminderKind.valueOf(inputData.getString(KEY_KIND) ?: "") }
            .getOrNull() ?: return Result.failure()

        NotificationHelper.show(applicationContext, taskId, title, priority, kind)
        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_TITLE = "title"
        const val KEY_PRIORITY = "priority"
        const val KEY_KIND = "kind"
    }
}