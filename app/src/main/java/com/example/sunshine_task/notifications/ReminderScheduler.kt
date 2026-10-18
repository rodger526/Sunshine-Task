package com.example.sunshine_task.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.sunshine_task.data.models.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Programa y cancela los avisos de cada tarea. WorkManager los conserva aunque se reinicie el celular. */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager by lazy { WorkManager.getInstance(context) }

    /** Reemplaza los avisos de la tarea por los que correspondan según su estado y fecha. */
    fun schedule(task: Task) {
        cancel(task.id)

        ReminderPlanner.plan(task).forEach { planned ->
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(planned.delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        ReminderWorker.KEY_TASK_ID to task.id,
                        ReminderWorker.KEY_TITLE to task.title,
                        ReminderWorker.KEY_PRIORITY to task.priority.name,
                        ReminderWorker.KEY_KIND to planned.kind.name
                    )
                )
                .build()

            workManager.enqueueUniqueWork(
                workName(task.id, planned.kind),
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    fun cancel(taskId: String) {
        ReminderKind.entries.forEach { kind ->
            workManager.cancelUniqueWork(workName(taskId, kind))
        }
    }

    private fun workName(taskId: String, kind: ReminderKind) = "recordatorio_${taskId}_${kind.name}"
}