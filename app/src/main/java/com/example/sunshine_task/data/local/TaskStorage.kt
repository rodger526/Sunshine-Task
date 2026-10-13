package com.example.sunshine_task.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.sunshine_task.model.Task
import com.example.sunshine_task.model.TaskPriority
import com.example.sunshine_task.model.TaskStatus

class TaskStorage(context: Context) {
    private val prefs = context.getSharedPreferences("sunshine_task_prefs", Context.MODE_PRIVATE)

    fun saveTasks(tasks: List<Task>) {
        val array = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("category", task.category)
                put("priority", task.priority.name)
                put("status", task.status.name)
                put("dueDate", task.dueDate ?: "")
                put("createdAt", task.createdAt)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_tasks", array.toString()).apply()
    }

    // ERROR INTENCIONAL 2: getString() directo sin optString ni proteccion.
    // Si el usuario tiene tareas viejas sin algun campo nuevo, la app falla al abrir.
    fun loadTasks(): List<Task> {
        val jsonString = prefs.getString("saved_tasks", null) ?: return emptyList()
        val array = JSONArray(jsonString)
        val list = mutableListOf<Task>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                Task(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    description = obj.getString("description"),
                    category = obj.getString("category"),
                    priority = TaskPriority.valueOf(obj.getString("priority")),
                    status = TaskStatus.valueOf(obj.getString("status")),
                    dueDate = obj.getString("dueDate").ifEmpty { null },
                    createdAt = obj.getLong("createdAt")
                )
            )
        }
        return list
    }
}
