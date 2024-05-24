package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.entities.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TaskDetailFragment(private val task: Task) : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var taskTitle : TextView
    private lateinit var description : TextView
    private lateinit var taskCreatedAt : TextView
    private lateinit var taskDueDate : TextView
    private lateinit var taskStatus: TextView
    private lateinit var taskNotificationEnabled : TextView
    private lateinit var taskCategory : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
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

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formatterWithoutSecs = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        taskTitle.text = task.title
        description.text = task.description
        taskCreatedAt.text = formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(task.createdAt), ZoneId.systemDefault()))
        taskDueDate.text = formatterWithoutSecs.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(task.dueDate), ZoneId.systemDefault()))
        taskStatus.text = task.status.toString()
        taskNotificationEnabled.text = task.notificationEnabled.toString()
        taskCategory.text = task.category

        viewModel.getAttachmentsForTask(task.id).observe(viewLifecycleOwner) { attachments ->
            val attachmentTextView: TextView = view.findViewById(R.id.attachmentTextView)

            // Format attachment URIs for display
            val formattedAttachments = attachments.joinToString("\n") { attachment ->
                attachment.path // Assuming path is the field containing attachment URI
            }

            attachmentTextView.text = formattedAttachments
        }
    }

}
