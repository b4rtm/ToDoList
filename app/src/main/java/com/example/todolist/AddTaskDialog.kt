package com.example.todolist;

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.util.Date
import java.util.Locale

class AddTaskDialog(context: Context) : Dialog(context) {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var addButton : Button
    private lateinit var cancelButton : Button
    private lateinit var dateButton : Button
    private var selectedDate: Long = 0
    private lateinit var selectedDateTextView: TextView
    private lateinit var categorySpinner: Spinner



    var listener: OnTaskAddedListener? = null

    interface OnTaskAddedListener {
        fun onTaskAdded(
            title: String,
            description: String,
            selectedDate: Long,
            selectedCategory: String
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_task)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        addButton = findViewById(R.id.addButton)
        cancelButton = findViewById(R.id.cancelButton)
        dateButton = findViewById(R.id.dateButton)
        selectedDateTextView = findViewById(R.id.selectedDateTextView)
        categorySpinner = findViewById(R.id.categorySpinner)

        val categories = context.resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter

        addButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val selectedCategory = categorySpinner.selectedItem as String

            if (selectedDate == 0L) {
                // Show error message or toast (e.g., using Toast.makeText)
                Toast.makeText(context, "Please select a date and time for the task", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            listener?.onTaskAdded(title, description, selectedDate, selectedCategory)
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context,
            { _, selectedYear, selectedMonth, selectedDay ->

                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(context,
                    { _, hourOfDay, minute ->
                        val selectedMillis = Calendar.getInstance().apply {
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                            set(Calendar.DAY_OF_MONTH, selectedDay)
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.timeInMillis
                        selectedDate = selectedMillis
                        selectedDateTextView.text = formatDateTime(selectedMillis)
                        dateButton.text = "Change Date & Time"
                    }, currentHour, currentMinute, true)
                timePickerDialog.show()
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun formatDateTime(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

}
