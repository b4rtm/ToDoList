package com.example.todolist;

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText

class AddTaskDialog(context: Context) : Dialog(context) {
private lateinit var titleEditText: EditText
private lateinit var descriptionEditText: EditText
private var listener: OnTaskAddedListener? = null

interface OnTaskAddedListener {
    fun onTaskAdded(title: String, description: String)
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_task)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        val addButton = findViewById<Button>(R.id.addButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        addButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            listener?.onTaskAdded(title, description)
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}
