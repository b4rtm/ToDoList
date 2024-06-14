package com.example.todolist

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.todolist.database.TaskDao
import com.example.todolist.database.TaskDatabase
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task
import com.example.todolist.entities.TaskStatus
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
        loadTasks()
    }

    fun loadTasks(): LiveData<List<Task>> {
        viewModelScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                taskDao.getAllTasks()
            }
            filterAndSortTasks(tasks)
        }
        return allTasks
    }

    private fun filterAndSortTasks(tasks: List<Task>) {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val hideCompleted = sharedPreferences.getBoolean("hideCompleted", false)
        val selectedCategory = sharedPreferences.getString("selectedCategory", "")
        val filteredTasks = tasks.filter { task ->
            (!hideCompleted || task.status == TaskStatus.IN_PROGRESS) &&
                    (selectedCategory.isNullOrEmpty() || task.category == selectedCategory)
        }.sortedBy { it.dueDate }

        allTasks.value = filteredTasks
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return allTasks
    }

    fun addTask(task: Task, attachments: List<Attachment>): LiveData<Long> {
        var result = MutableLiveData<Long>()

        viewModelScope.launch {
            val taskId = withContext(Dispatchers.IO) {
                taskDao.insert(task)
            }
            withContext(Dispatchers.Main) {
            }
            result.postValue(taskId)
            attachments.forEach { it.taskId = taskId }
            taskDao.insertAttachments(attachments)
            loadTasks()
        }
        return result
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskDao.delete(task)
            }
            loadTasks()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskDao.update(task)
                loadTasks()
                getAllTasks()
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

    fun getTaskSync(taskId: Long): Task? {
        return taskDao.getTaskSync(taskId)
    }

    fun addAttachment(attachment: Attachment) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insertAttachment(attachment)
        }
    }

    fun hasAttachments(taskId: Long): LiveData<Boolean> {
        return taskDao.hasAttachments(taskId)
    }


}
