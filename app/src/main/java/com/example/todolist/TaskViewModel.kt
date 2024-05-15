package com.example.todolist;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.Task

//class TaskViewModel : ViewModel() {
//    val taskList: MutableLiveData<MutableList<Task>> by lazy {
//        MutableLiveData<MutableList<Task>>()
//    }
//
//    init {
//        // Tutaj możesz zainicjować listę zadań, jeśli to konieczne
//        taskList.value = mutableListOf()
//    }
//
//    fun addTask(task: Task) {
//        val currentList = taskList.value
//        Log.d("TaskViewModel", "Task added: ${task.title}")
//        currentList?.add(task)
//        taskList.value = currentList
//
//    }
//}
