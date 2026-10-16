package com.example.sunshine_task.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.sunshine_task.model.TaskAttachment

class AttachmentStorage(context: Context) {
    private val prefs = context.getSharedPreferences("sunshine_task_attachments", Context.MODE_PRIVATE)

    fun saveAttachments(taskId: String, attachments: List<TaskAttachment>) {
        val array = JSONArray()
        attachments.forEach { item ->
            val obj = JSONObject().apply {
                put("uri", item.uri)
            }
            array.put(obj)
        }
        prefs.edit().putString("attachments_$taskId", array.toString()).apply()
    }

    fun loadAttachments(taskId: String): List<TaskAttachment> {
        val jsonString = prefs.getString("attachments_$taskId", null) ?: return emptyList()
        val list = mutableListOf<TaskAttachment>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val uri = obj.optString("uri", "")
                if (uri.isNotEmpty()) {
                    list.add(TaskAttachment(uri = uri))
                }
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }
}
