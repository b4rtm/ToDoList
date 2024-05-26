// AddTaskDialog.kt
package com.example.todolist

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.todolist.utils.DateTimeUtils

class AddTaskDialog(
    context: Context,
    private val launchFilePicker: () -> Unit
) : Dialog(context) {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var addButton: Button
    private lateinit var dateButton: Button
    private var selectedDate: Long = 0
    private lateinit var selectedDateTextView: TextView
    private lateinit var categorySpinner: Spinner
    private lateinit var selectImageButton: Button
    private lateinit var attachmentsContainer: LinearLayout

    private val attachmentUris = mutableListOf<Uri>()

    var listener: OnTaskAddedListener? = null

    interface OnTaskAddedListener {
        fun onTaskAdded(
            title: String,
            description: String,
            selectedDate: Long,
            selectedCategory: String,
            attachments: MutableList<Uri>
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_task)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        addButton = findViewById(R.id.addButton)
        dateButton = findViewById(R.id.dateButton)
        selectedDateTextView = findViewById(R.id.selectedDateTextView)
        categorySpinner = findViewById(R.id.categorySpinner)
        selectImageButton = findViewById(R.id.selectImageButton)
        attachmentsContainer = findViewById(R.id.attachmentsContainer)

        val categories = context.resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter

        addButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val selectedCategory = categorySpinner.selectedItem as String

            if (selectedDate == 0L) {
                Toast.makeText(context, "Please select a date and time for the task", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            listener?.onTaskAdded(title, description, selectedDate, selectedCategory, attachmentUris)
            dismiss()
        }

        selectImageButton.setOnClickListener {
            launchFilePicker()
        }

        dateButton.setOnClickListener {
            DateTimeUtils.showDatePickerDialog(context) { milis ->
                selectedDateTextView.text = DateTimeUtils.formatDateTime(milis)
                selectedDate = milis
            }
        }
    }

    fun setAttachmentUris(uris: List<Uri>) {
        attachmentUris.clear()
        attachmentUris.addAll(uris)
        displayAttachments()
    }

    private fun displayAttachments() {
        attachmentsContainer.removeAllViews()
        attachmentUris.forEach { uri ->
            val attachmentView = layoutInflater.inflate(R.layout.attachment_item, null)
            val attachmentTextView = attachmentView.findViewById<TextView>(R.id.attachmentTextView)
            val deleteButton = attachmentView.findViewById<Button>(R.id.deleteButton)

            attachmentTextView.text = uri.toString()

            deleteButton.setOnClickListener {
                attachmentUris.remove(uri)
                displayAttachments()
            }

            attachmentsContainer.addView(attachmentView)
        }
    }
}
