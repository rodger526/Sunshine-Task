package com.example.sunshine_task.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sunshine_task.data.local.dao.TaskDao
import com.example.sunshine_task.data.local.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
