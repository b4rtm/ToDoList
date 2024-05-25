package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.todolist.entities.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.Manifest
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import com.example.todolist.entities.TaskStatus
import com.example.todolist.utils.DateTimeUtils


class TaskDetailsFragment(private val task: Task) : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var taskTitle : TextView
    private lateinit var description : TextView
    private lateinit var taskCreatedAt : TextView
    private lateinit var taskDueDate : TextView
    private lateinit var attachmentContainer: LinearLayout
    private lateinit var confirmUpdateButton: ImageButton
    private lateinit var calendarButton: ImageButton
    private lateinit var updateCategorySpinner: Spinner

    private lateinit var switchDone: SwitchCompat
    private lateinit var switchNotification: SwitchCompat

    private var selectedDate = task.dueDate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, perform the action to load images
            loadImages()
        } else {
            // Permission is denied, handle accordingly (e.g., show a message)
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)


        taskTitle = view.findViewById(R.id.taskTitle)
        description = view.findViewById(R.id.taskDescription)
        taskDueDate = view.findViewById(R.id.taskDueDate)
        attachmentContainer = view.findViewById(R.id.attachmentContainer)
        taskCreatedAt = view.findViewById(R.id.taskCreatedAt)
        confirmUpdateButton = view.findViewById(R.id.confirmUpdate)
        switchDone = view.findViewById(R.id.switchDone)
        updateCategorySpinner = view.findViewById(R.id.updateCategorySpinner)
        switchNotification = view.findViewById(R.id.switchNoti)
        calendarButton = view.findViewById(R.id.calendarButton)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formatterWithoutSecs = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        taskTitle.text = task.title
        description.text = task.description
        taskCreatedAt.text = formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(task.createdAt), ZoneId.systemDefault()))
        taskDueDate.text = formatterWithoutSecs.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(task.dueDate), ZoneId.systemDefault()))

        if(task.notificationEnabled)
            switchNotification.isChecked = true

        if(task.status == TaskStatus.COMPLETED)
            switchDone.isChecked = true

        val categories = resources.getStringArray(R.array.categories)
        updateCategorySpinner.setSelection(categories.indexOf(task.category))

        confirmUpdateButton.setOnClickListener {
            updateTask(view)
            requireActivity().supportFragmentManager.popBackStack()
        }

        calendarButton.setOnClickListener {
            context?.let { it1 ->
                DateTimeUtils.showDatePickerDialog(it1){ milis -> taskDueDate.text = DateTimeUtils.formatDateTime(milis)
                    selectedDate = milis
                }
            }
        }
    }


    private fun updateTask(view: View) {
        val updatedTitle = view.findViewById<EditText>(R.id.taskTitle).text.toString()
        val updatedDescription = view.findViewById<EditText>(R.id.taskDescription).text.toString()
        val updatedDueDateInMilis = selectedDate

        val updatedStatus : TaskStatus = if(switchDone.isChecked)
            TaskStatus.COMPLETED
        else
            TaskStatus.IN_PROGRESS

        val updatedNotificationEnabled = switchDone.isChecked
        val updatedCategory = updateCategorySpinner.selectedItem.toString()

        val updatedTask = Task(
            id = task.id,
            title = updatedTitle,
            description = updatedDescription,
            status = updatedStatus,
            dueDate = updatedDueDateInMilis,
            category = updatedCategory,
            notificationEnabled = updatedNotificationEnabled,
        )

        viewModel.updateTask(updatedTask)
    }



    private fun loadImage(imageUri: String) {
        val imageView = ImageView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 16, 0)
        imageView.layoutParams = layoutParams

        Glide.with(this)
            .load(imageUri)
            .override(300, 300)
            .into(imageView)

        attachmentContainer.addView(imageView)
    }

    private fun loadImages() {
        viewModel.getAttachmentsForTask(task.id).observe(viewLifecycleOwner) { attachments ->
            attachments.forEach { attachment ->
                loadImage(attachment.path)
            }
        }
    }

}
