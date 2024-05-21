package com.example.todolist;

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import java.util.Date
import java.util.Locale

class AddTaskDialog(context: Context) : Dialog(context) {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var addButton : Button
    private lateinit var cancelButton : Button
    private lateinit var dateButton : Button
    private var selectedDate: Long? = null
    private lateinit var selectedDateTextView: TextView



    var listener: OnTaskAddedListener? = null

    interface OnTaskAddedListener {
        fun onTaskAdded(title: String, description: String, selectedDate: Long)
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

        addButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            selectedDate?.let { it1 -> listener?.onTaskAdded(title, description, it1) }
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
