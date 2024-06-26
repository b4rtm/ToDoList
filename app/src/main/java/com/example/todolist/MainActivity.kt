package com.example.todolist

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.components.AddTaskDialog
import com.example.todolist.components.SettingsDialogFragment
import com.example.todolist.components.TaskDetailsFragment
import com.example.todolist.database.TaskDatabase
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task
import com.example.todolist.utils.ImageUtils
import com.example.todolist.utils.NotificationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), AddTaskDialog.OnTaskAddedListener,
    TaskDetailsFragment.OnTaskUpdateListener, SettingsDialogFragment.SettingsDialogListener {

    lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var taskDatabase: TaskDatabase

    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var addTaskDialog: AddTaskDialog
    private lateinit var searchEditText: EditText
    private lateinit var settingsButton: ImageButton

    override fun onSettingsSaved() {
        viewModel.loadTasks()
    }


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
        settingsButton = findViewById(R.id.settingsButton)
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

        NotificationUtils.createNotificationChannel(this)
        handleNotificationIntent(intent)


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

        settingsButton.setOnClickListener {
            val settingsDialog = SettingsDialogFragment()
            settingsDialog.show(supportFragmentManager, "SettingsDialogFragment")
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            showNotificationPermissionDialog()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleNotificationIntent(it)
        }
    }

    private fun handleNotificationIntent(intent: Intent) {
        if (intent.getBooleanExtra("navigate_to_task", false)) {
            Log.d("MainActivity", "ESSSAAAAAA")
            val taskId = intent.getLongExtra("task_id", -1)
            if (taskId != -1L) {
                navigateToTask(taskId)
            }
        }
    }

    private fun navigateToTask(taskId: Long) {
        Thread {
            val task = viewModel.getTaskSync(taskId)
            task?.let {
                runOnUiThread {
                    Log.d("MainActivity", "Navigating to task with ID: $taskId")
                    val bundle = Bundle()
                    bundle.putLong("TASK_ID", task.id)

                    val oldFragment =
                        supportFragmentManager.findFragmentByTag(TaskDetailsFragment::class.java.simpleName)

                    if (oldFragment != null && oldFragment is TaskDetailsFragment) {
                        return@runOnUiThread
                    }
                    val taskDetailFragment = TaskDetailsFragment.newInstance(task.id)

                    taskDetailFragment.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.main,
                            taskDetailFragment,
                            TaskDetailsFragment::class.java.simpleName
                        )
                        .addToBackStack(null)
                        .commit()
                    fab.hide()
                }
            }
        }.start()
    }


    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications")
            .setMessage("Do you want to enable notifications?")
            .setPositiveButton("Yes") { dialog, _ ->
                val intent = Intent().apply {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", packageName)
                    putExtra("app_uid", applicationInfo.uid)
                    putExtra("android.provider.extra.APP_PACKAGE", packageName)
                }
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun filterTasks(query: String) {
        viewModel.getAllTasks().observe(this) { tasks ->
            val filteredTasks = tasks.filter {
                it.title.contains(query, ignoreCase = true)
            }
            adapter.updateTasks(filteredTasks)
        }
    }

    private fun onClickTaskAction(task: Task) {
        Log.d("MainActivity", "Navigating to task with ID: ${task.id}")
        val bundle = Bundle()
        bundle.putLong("TASK_ID", task.id)
        val taskDetailFragment = TaskDetailsFragment.newInstance(task.id)

        taskDetailFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.main, taskDetailFragment,TaskDetailsFragment::class.java.simpleName)
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
        viewModel.addTask(newTask, attachmentEntities).observe(this) { taskId ->
            if (taskId != null) {
                newTask.id = taskId
                if (newTask.notificationEnabled)
                    NotificationUtils.setNotification(this.applicationContext, newTask)
            }
        }
    }


    fun onDeleteButtonClick(task: Task) {
        viewModel.deleteTask(task)
    }

    override fun onResume() {
        super.onResume()
        fab.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fab.show()
    }

    override fun onTaskUpdated() {
        viewModel.loadTasks().observe(this) { tasks ->
            adapter.updateTasks(tasks)
        }
    }

}