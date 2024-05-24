package com.example.todolist

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.entities.Task

@Database(entities = [Task::class], version = 2)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

}
