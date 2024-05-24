package com.example.todolist

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task

@Database(entities = [Task::class, Attachment::class], version = 3)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

}
