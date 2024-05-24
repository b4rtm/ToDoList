package com.example.todolist.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attachments")
data class Attachment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val path: String,
)