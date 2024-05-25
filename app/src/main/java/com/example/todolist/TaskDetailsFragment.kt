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
import android.os.Build


class TaskDetailsFragment(private val task: Task) : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var taskTitle : TextView
    private lateinit var description : TextView
    private lateinit var taskCreatedAt : TextView
    private lateinit var taskDueDate : TextView
    private lateinit var taskStatus: TextView
    private lateinit var taskNotificationEnabled : TextView
    private lateinit var taskCategory : TextView
    private lateinit var attachmentContainer: LinearLayout

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
        taskCreatedAt = view.findViewById(R.id.taskCreatedAt)
        taskDueDate = view.findViewById(R.id.taskDueDate)
        taskStatus = view.findViewById(R.id.taskStatus)
        taskNotificationEnabled = view.findViewById(R.id.taskNotificationEnabled)
        taskCategory = view.findViewById(R.id.taskCategory)
        attachmentContainer = view.findViewById(R.id.attachmentContainer)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formatterWithoutSecs = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        taskTitle.text = task.title
        description.text = task.description
        taskCreatedAt.text = formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(task.createdAt), ZoneId.systemDefault()))
        taskDueDate.text = formatterWithoutSecs.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(task.dueDate), ZoneId.systemDefault()))
        taskStatus.text = task.status.toString()
        taskNotificationEnabled.text = task.notificationEnabled.toString()
        taskCategory.text = task.category

    }

    private fun loadImage(imageUri: String) {
        val imageView = ImageView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 16, 0) // Adjust margins as needed
        imageView.layoutParams = layoutParams

        Glide.with(this)
            .load(imageUri)
            .override(300, 300)
            .into(imageView)

        attachmentContainer.addView(imageView)
    }

    private fun loadImages() {
        // Assuming you have a ViewModel method to fetch attachments for a task
        viewModel.getAttachmentsForTask(task.id).observe(viewLifecycleOwner) { attachments ->
            // Iterate through the list of attachments and load each image
            attachments.forEach { attachment ->
                loadImage(attachment.path)
            }
        }
    }

}
