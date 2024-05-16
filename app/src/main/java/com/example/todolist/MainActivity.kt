package com.example.todolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), AddTaskDialog.OnTaskAddedListener {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerView)
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)


        adapter = TaskAdapter(ArrayList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        fab.setOnClickListener {
            val dialog = AddTaskDialog(this)
            dialog.listener = this
            dialog.show()
        }
    }

    override fun onTaskAdded(title: String, description: String) {
        val newTask = Task(title, description)
        viewModel.addTask(newTask)
        adapter.addTask(newTask)
    }
}