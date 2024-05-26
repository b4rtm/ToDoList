package com.example.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<Attachment>)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM attachments WHERE taskId = :taskID")
    fun getAttachmentsByTaskId(taskID: Long): LiveData<List<Attachment>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): LiveData<Task>

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}