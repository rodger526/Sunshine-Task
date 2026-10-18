package com.example.sunshine_task.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.sunshine_task.model.Task
import com.example.sunshine_task.model.TaskPriority
import com.example.sunshine_task.model.TaskStatus
import java.util.UUID

class TaskStorage(context: Context) {
    private val prefs = context.getSharedPreferences("sunshine_task_prefs", Context.MODE_PRIVATE)

    fun saveTasks(tasks: List<Task>) {
        // Depuracion de duplicados y tareas sin titulo
        val cleanTasks = tasks
            .filter { it.title.isNotBlank() }
            .distinctBy { it.id }

        val array = JSONArray()
        cleanTasks.forEach { task ->
            val obj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title.trim())
                put("description", task.description.trim())
                put("category", task.category.ifBlank { "General" })
                put("priority", task.priority.name)
                put("status", task.status.name)
                put("dueDate", task.dueDate ?: "")
                put("createdAt", task.createdAt)
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
                            priority = try { TaskPriority.valueOf(priorityStr) } catch (_: Exception) { TaskPriority.MEDIA },
                            status = try { TaskStatus.valueOf(statusStr) } catch (_: Exception) { TaskStatus.PENDIENTE },
                            dueDate = obj.optString("dueDate", "").ifEmpty { null },
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                } catch (_: Exception) {
                    // Ignora entradas corruptas sin interrumpir el flujo
                }
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list.distinctBy { it.id }
    }

    fun deleteTask(taskId: String) {
        val currentTasks = loadTasks().toMutableList()
        currentTasks.removeAll { it.id == taskId }
        saveTasks(currentTasks)
    }

    fun clearAll() {
        prefs.edit().remove("saved_tasks").apply()
    }
}
