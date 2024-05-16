package com.example.todolist;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
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



    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task) // Use Coroutine for asynchronous task
        }
    }
}
