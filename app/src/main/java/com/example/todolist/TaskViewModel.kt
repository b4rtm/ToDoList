package com.example.todolist;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao: TaskDao
    val allTasks: LiveData<List<Task>>

    init {
        val database = Room.databaseBuilder(application, TaskDatabase::class.java, "task_database")
            .build()
        taskDao = database.taskDao()
        allTasks = taskDao.getAllTasks()
    }



    fun addTask(task: Task, attachments: List<Attachment>) {
        viewModelScope.launch {
            val taskId = taskDao.insert(task) // Use Coroutine for asynchronous task
            attachments.forEach { it.taskId = taskId }
            taskDao.insertAttachments(attachments)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }

    fun getAttachmentsForTask(taskId: Long): LiveData<List<Attachment>> {
        return taskDao.getAttachmentsByTaskId(taskId)
    }

//    fun getTask(id: Long): LiveData<Task>? {
//        return taskDao.getTodoById(id)
//    }
}
