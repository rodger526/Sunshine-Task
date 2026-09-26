package com.example.sunshine_task.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.sunshine_task.model.Task

class TaskStorage(context: Context) {
    private val prefs = context.getSharedPreferences("sunshine_task_prefs", Context.MODE_PRIVATE)

    fun saveTasks(tasks: List<Task>) {
        val array = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject().apply {
                // ERROR INTENCIONAL 1: Solo se guardan 3 campos, perdiendo id, category, priority, dueDate y createdAt
                put("title", task.title)
                put("description", task.description)
                put("status", task.status.name)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_tasks", array.toString()).apply()
    }
}
