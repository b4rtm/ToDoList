package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    var dueDate: Long,
    val status: TaskStatus = TaskStatus.IN_PROGRESS,
    val notificationEnabled: Boolean = true,
    val category: String? = null,
    val attachments: List<Attachment>? = null,
)

enum class TaskStatus {
    IN_PROGRESS,
    COMPLETED
}

data class Attachment(
    val type: AttachmentType,
    val path: String
)

enum class AttachmentType {
    IMAGE,
    FILE
}
