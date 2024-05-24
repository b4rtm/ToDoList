package com.example.todolist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entities.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task): Long

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Delete
    suspend fun delete(task: Task)
}