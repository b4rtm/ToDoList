package com.example.todolist;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    val taskList: MutableLiveData<MutableList<Task>> by lazy {
        MutableLiveData<MutableList<Task>>()
    }

    init {
        taskList.value = mutableListOf()
    }

    fun addTask(task: Task) {
        val currentList = taskList.value
        Log.d("TaskViewModel", "Task added: ${task.title}")
        currentList?.add(task)
        taskList.value = currentList

    }
}
