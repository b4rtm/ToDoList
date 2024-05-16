package com.example.todolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AddTaskDialog.OnTaskAddedListener {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var taskDatabase: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerView)
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)




        adapter = TaskAdapter(ArrayList())
        adapter.setOnDeleteClickListener { task ->
            viewModel.deleteTask(task)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val dialog = AddTaskDialog(this)
            dialog.listener = this
            dialog.show()
        }

        taskDatabase = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java, "task_database"
        ).build()

        viewModel.allTasks.observe(this) { tasks ->
            adapter.updateTasks(tasks)
        }
    }

    override fun onTaskAdded(title: String, description: String) {
        val newTask = Task(title = title, description = description)
        viewModel.addTask(newTask) // Delegate to ViewModel for asynchronous insert
        adapter.addTask(newTask) // Update UI immediately (optional)
    }

    fun onDeleteButtonClick(task: Task) {
        viewModel.deleteTask(task) // Call ViewModel to delete the task
    }


}