package com.example.sunshine_task.data.local

import android.content.Context
import com.example.sunshine_task.data.models.TaskAttachment
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("sunshine_task_attachments", Context.MODE_PRIVATE)

    fun saveAttachments(taskId: String, attachments: List<TaskAttachment>) {
        val array = JSONArray()
        attachments.distinctBy { it.uri }.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("uri", item.uri)
                    put("name", item.name)
                    put("mimeType", item.mimeType)
                    put("sizeText", item.sizeText)
                }
            )
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
                    list.add(
                        TaskAttachment(
                            uri = uri,
                            name = obj.optString("name", "Archivo adjunto"),
                            mimeType = obj.optString("mimeType", "*/*"),
                            sizeText = obj.optString("sizeText", "")
                        )
                    )
                }
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list.distinctBy { it.uri }
    }

    fun removeAttachment(taskId: String, uriToRemove: String) {
        saveAttachments(taskId, loadAttachments(taskId).filter { it.uri != uriToRemove })
    }

    fun clearAttachments(taskId: String) {
        prefs.edit().remove("attachments_$taskId").apply()
    }
}