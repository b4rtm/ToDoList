package com.example.todolist.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    var dueDate: Long,
    val status: TaskStatus = TaskStatus.IN_PROGRESS,
    val notificationEnabled: Boolean = true,
    val category: String? = null,
)

enum class TaskStatus {
    IN_PROGRESS,
    COMPLETED
}

