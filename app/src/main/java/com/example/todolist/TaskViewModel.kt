package com.example.todolist;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.todolist.database.TaskDao
import com.example.todolist.database.TaskDatabase
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao: TaskDao
    var allTasks = MutableLiveData<List<Task>>()

    init {
        val database = Room.databaseBuilder(application, TaskDatabase::class.java, "task_database")
            .build()
        taskDao = database.taskDao()
        viewModelScope.launch {
            allTasks.postValue(taskDao.getAllTasks())
        }
    }

    fun getAllTasks(): LiveData<List<Task>> {
        val allTasksLiveData = MutableLiveData<List<Task>>()
        viewModelScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                taskDao.getAllTasks() // Perform database operation on IO thread
            }
            // Update LiveData on the main thread
            withContext(Dispatchers.Main) {
                allTasksLiveData.value = tasks
            }
        }
        return allTasksLiveData
    }

    fun addTask(task: Task, attachments: List<Attachment>) {
        viewModelScope.launch {
            val taskId = taskDao.insert(task) // Use Coroutine for asynchronous task
            attachments.forEach { it.taskId = taskId }
            taskDao.insertAttachments(attachments)
            // Update LiveData after adding task
            withContext(Dispatchers.Main) {
                allTasks.value = taskDao.getAllTasks()
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
            // Update LiveData after deleting task
            withContext(Dispatchers.Main) {
                allTasks.value = taskDao.getAllTasks()
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
            // Update LiveData after updating task
            withContext(Dispatchers.Main) {
                allTasks.value = taskDao.getAllTasks()
            }
        }
    }

    fun getAttachmentsForTaskLive(taskId: Long): LiveData<List<Attachment>> {
        return taskDao.getAttachmentsByTaskIdLive(taskId)
    }

    fun getAttachmentsForTask(taskId: Long): List<Attachment> {
        var attachments: List<Attachment> = mutableListOf()
        viewModelScope.launch {
            attachments = taskDao.getAttachmentsByTaskId(taskId)
        }
        return attachments
    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteAttachment(attachment)
        }
    }

    fun getTask(taskId: Long): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }
}
