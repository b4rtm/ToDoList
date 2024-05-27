package com.example.todolist

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.database.TaskDatabase
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task
import com.example.todolist.utils.ImageUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), AddTaskDialog.OnTaskAddedListener,
    TaskDetailsFragment.OnTaskUpdateListener {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var taskDatabase: TaskDatabase

    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var addTaskDialog: AddTaskDialog
    private lateinit var searchEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getContent =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
                addTaskDialog.setAttachmentUris(uris)
            }

        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.editTextSearch)
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        adapter = TaskAdapter(ArrayList(), viewModel)
        adapter.setOnDeleteClickListener { task ->
            viewModel.deleteTask(task)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener { task ->
            onClickTaskAction(task)
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

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTasks(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun filterTasks(query: String) {
        viewModel.allTasks.observe(this) { tasks ->
            val filteredTasks = tasks.filter {
                it.title.contains(query, ignoreCase = true)
            }
            adapter.updateTasks(filteredTasks)
        }
    }

    private fun onClickTaskAction(task: Task) {
        val bundle = Bundle()
        bundle.putLong("TASK_ID", task.id)
        val taskDetailFragment = TaskDetailsFragment(task)
        taskDetailFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.main, taskDetailFragment)
            .addToBackStack(null)
            .commit()
        fab.hide()
    }

    override fun onTaskAdded(
        title: String,
        description: String,
        selectedDate: Long,
        selectedCategory: String,
        attachments: MutableList<Uri>
    ) {
        val newTask = Task(
            title = title,
            description = description,
            dueDate = selectedDate,
            category = selectedCategory
        )
        val attachmentEntities = attachments.mapNotNull { uri ->
            ImageUtils.saveImageToInternalStorage(this, uri)?.let { path ->
                Attachment(taskId = newTask.id, path = path)
            }
        }
        viewModel.addTask(newTask, attachmentEntities)
    }


    fun onDeleteButtonClick(task: Task) {
        viewModel.deleteTask(task)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fab.show()
    }

    override fun onTaskUpdated() {
        viewModel.getAllTasks().observe(this) { tasks ->
            adapter.updateTasks(tasks)
        }
    }

}