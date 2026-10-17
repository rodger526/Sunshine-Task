package com.example.sunshine_task.data.models

data class TaskAttachment(
    val uri: String,
    val name: String = "Archivo adjunto",
    val mimeType: String = "*/*",
    val sizeText: String = ""
)
