package com.example.sunshine_task.data.local

import android.content.Context
import org.json.JSONArray
import com.example.sunshine_task.data.models.Task
import com.example.sunshine_task.data.models.Priority
import com.example.sunshine_task.data.models.Status
import java.util.UUID

class TaskStorage(context: Context) {
    private val prefs = context.getSharedPreferences("sunshine_task_prefs", Context.MODE_PRIVATE)

    fun saveTasks(tasks: List<Task>) {
        val array = JSONArray()
        tasks.forEach { task ->
            val obj = org.json.JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("category", task.category)
                put("priority", task.priority.name)
                put("status", task.status.name)
                put("dueDate", task.dueDate)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_tasks", array.toString()).apply()
    }

    fun loadTasks(): List<Task> {
        val jsonString = prefs.getString("saved_tasks", null) ?: return emptyList()
        val list = mutableListOf<Task>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                try {
                    val obj = array.getJSONObject(i)
                    val title = obj.optString("title", "").trim()
                    if (title.isEmpty()) continue

                    val priorityStr = obj.optString("priority", "MEDIA")
                    val statusStr = obj.optString("status", "PENDIENTE")

                    list.add(
                        Task(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            title = title,
                            description = obj.optString("description", ""),
                            category = obj.optString("category", "General"),
                            priority = try { Priority.valueOf(priorityStr) } catch (_: Exception) { Priority.MEDIA },
                            status = try { Status.valueOf(statusStr) } catch (_: Exception) { Status.PENDIENTE },
                            dueDate = obj.optString("dueDate", "2026-12-31")
                        )
                    )
                } catch (_: Exception) {
                    // Ignora el registro corrupto puntual sin tronar el resto
                }
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }
}
