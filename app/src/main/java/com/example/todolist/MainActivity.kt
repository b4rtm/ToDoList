package com.example.todolist

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), AddTaskDialog.OnTaskAddedListener {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var taskDatabase: TaskDatabase

    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var addTaskDialog: AddTaskDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            addTaskDialog.setAttachmentUris(uris)
        }

        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerView)
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        adapter = TaskAdapter(ArrayList())
        adapter.setOnDeleteClickListener { task ->
            viewModel.deleteTask(task)
        }



        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener { task ->
            val bundle = Bundle()
            bundle.putLong("TASK_ID", task.id)
            val taskDetailFragment = TaskDetailFragment(task)
            taskDetailFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.main, taskDetailFragment)
                .addToBackStack(null)
                .commit()
        }

        fab.setOnClickListener {
            addTaskDialog = AddTaskDialog(this) {
                getContent.launch("*/*")
            }

            addTaskDialog.listener = this
            addTaskDialog.show()
        }
        taskDatabase = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java, "task_database"
        ).build()


        viewModel.allTasks.observe(this) { tasks ->
            adapter.updateTasks(tasks)
        }
    }

    override fun onTaskAdded(
        title: String,
        description: String,
        selectedDate: Long,
        selectedCategory: String,
        attachments: MutableList<Uri>
    ) {
        val newTask = Task(title = title, description = description, dueDate = selectedDate, category = selectedCategory)
        val attachmentEntities = attachments.map { uri ->
            Attachment(taskId = newTask.id, path = uri.toString())
        }
        viewModel.addTask(newTask, attachmentEntities)
        adapter.addTask(newTask)
    }


    fun onDeleteButtonClick(task: Task) {
        viewModel.deleteTask(task)
    }


}