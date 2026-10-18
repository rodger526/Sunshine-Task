package com.example.sunshine_task

import android.app.Application
import com.example.sunshine_task.data.local.dao.TaskDao
import com.example.sunshine_task.data.local.mapper.toDomain
import com.example.sunshine_task.notifications.NotificationHelper
import com.example.sunshine_task.notifications.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SunshineTaskApp : Application() {

    @Inject lateinit var taskDao: TaskDao
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)

        // Reprograma los avisos de las tareas que ya existían antes de esta función.
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.getAll().forEach { reminderScheduler.schedule(it.toDomain()) }
        }
    }
}