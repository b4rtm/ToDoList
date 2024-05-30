package com.example.todolist.components

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.TaskViewModel
import com.example.todolist.entities.Attachment
import com.example.todolist.entities.Task
import com.example.todolist.entities.TaskStatus
import com.example.todolist.utils.DateTimeUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TaskDetailsFragment : Fragment() {

    companion object {
        private const val ARG_TASK_ID = "task_id"

        @JvmStatic
        fun newInstance(taskId: Long) = TaskDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_TASK_ID, taskId)
            }
        }
    }

    private lateinit var viewModel: TaskViewModel
    private lateinit var taskTitle: TextView
    private lateinit var description: TextView
    private lateinit var taskCreatedAt: TextView
    private lateinit var taskDueDate: TextView
    private lateinit var attachmentContainer: LinearLayout
    private lateinit var confirmUpdateButton: ImageButton
    private lateinit var calendarButton: ImageButton
    private lateinit var updateCategorySpinner: Spinner
    private lateinit var switchDone: SwitchCompat
    private lateinit var switchNotification: SwitchCompat
    private lateinit var addAttachmentButton: Button
    private lateinit var task: Task

    private lateinit var mainActivity: MainActivity

    private var selectedDate: Long = 0L
    private var taskId: Long = 0L
    private var taskUpdateListener: OnTaskUpdateListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTaskUpdateListener) {
            taskUpdateListener = context
            mainActivity = context as MainActivity
        } else {
            throw RuntimeException("$context must implement OnTaskUpdateListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        taskUpdateListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        taskId = arguments?.getLong("TASK_ID", -1)!!

        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadImages()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val attachment = Attachment(
                taskId = task.id,
                path = it.toString()
            )
            viewModel.addAttachment(attachment)
            taskUpdateListener?.onTaskUpdated()
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
        addAttachmentButton = view.findViewById(R.id.addAttachmentButton)

        viewModel.getTask(taskId).observe(viewLifecycleOwner) { task1 ->
            if (task1 != null) {
                task = task1
                initFields()
            } else {
            }
        }

        confirmUpdateButton.setOnClickListener {
            updateTask(view)
            hideKeyboard()
            mainActivity.fab.show()
            requireActivity().supportFragmentManager.popBackStack()
            taskUpdateListener?.onTaskUpdated()
        }

        calendarButton.setOnClickListener {
            context?.let { it1 ->
                DateTimeUtils.showDatePickerDialog(it1) { milis ->
                    taskDueDate.text = DateTimeUtils.formatDateTime(milis)
                    selectedDate = milis
                }
            }
        }

        addAttachmentButton.setOnClickListener {
            pickImageLauncher.launch("*/*")
        }

    }

    private fun initFields() {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formatterWithoutSecs = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        taskTitle.text = task.title
        description.text = task.description
        taskCreatedAt.text = formatter.format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(task.createdAt),
                ZoneId.systemDefault()
            )
        )
        taskDueDate.text = formatterWithoutSecs.format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(task.dueDate),
                ZoneId.systemDefault()
            )
        )

        switchNotification.isChecked = task.notificationEnabled

        switchDone.isChecked = task.status == TaskStatus.COMPLETED

        val categories = resources.getStringArray(R.array.categories)
        updateCategorySpinner.setSelection(categories.indexOf(task.category))
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun updateTask(view: View) {
        val updatedTitle = view.findViewById<EditText>(R.id.taskTitle).text.toString()
        val updatedDescription = view.findViewById<EditText>(R.id.taskDescription).text.toString()
        val updatedDueDateInMilis = selectedDate

        val updatedStatus: TaskStatus = if (switchDone.isChecked)
            TaskStatus.COMPLETED
        else
            TaskStatus.IN_PROGRESS

        val updatedNotificationEnabled = switchNotification.isChecked
        val updatedCategory = updateCategorySpinner.selectedItem.toString()

        val updatedTask = Task(
            id = task.id,
            title = updatedTitle,
            description = updatedDescription,
            status = updatedStatus,
            dueDate = updatedDueDateInMilis,
            category = updatedCategory,
            notificationEnabled = updatedNotificationEnabled
        )

        viewModel.updateTask(updatedTask)
    }

    private fun loadImage(attachment: Attachment) {
        val attachmentView = layoutInflater.inflate(R.layout.image_view, null)
        val imageView = attachmentView.findViewById<ImageView>(R.id.attachmentImageView)
        val deleteButton = attachmentView.findViewById<Button>(R.id.deleteButton)

        Glide.with(this)
            .load(attachment.path)
            .override(300, 300)
            .into(imageView)

        deleteButton.setOnClickListener {
            viewModel.deleteAttachment(attachment)
            attachmentContainer.removeView(attachmentView)
        }

        attachmentContainer.addView(attachmentView)
    }

    private fun loadImages() {
        viewModel.getAttachmentsForTaskLive(taskId).observe(viewLifecycleOwner) { attachments ->
            updateAttachmentViews(attachments)
        }
    }

    private fun updateAttachmentViews(attachments: List<Attachment>) {
        attachmentContainer.removeAllViews()
        attachments.forEach { attachment ->
            loadImage(attachment)
        }
    }

    interface OnTaskUpdateListener {
        fun onTaskUpdated()
    }

    override fun onStart() {
        super.onStart()
        mainActivity.fab.hide()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.fab.hide()
    }
}
