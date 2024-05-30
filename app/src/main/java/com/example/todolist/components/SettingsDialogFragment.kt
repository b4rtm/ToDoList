package com.example.todolist.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import com.example.todolist.R

class SettingsDialogFragment : DialogFragment() {

    interface SettingsDialogListener {
        fun onSettingsSaved()
    }

    private var listener: SettingsDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as SettingsDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement SettingsDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_settings, null)

        val checkBoxHideCompleted = view.findViewById<CheckBox>(R.id.checkBoxHideCompleted)
        val spinnerCategories = view.findViewById<Spinner>(R.id.spinnerCategories)
        val editTextNotificationTime = view.findViewById<EditText>(R.id.editTextNotificationTime)

        val categories = resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategories.adapter = adapter

        val sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        checkBoxHideCompleted.isChecked = sharedPreferences.getBoolean("hideCompleted", false)
        val selectedCategory = sharedPreferences.getString("selectedCategory", "")
        val notificationTime = sharedPreferences.getInt("notificationTime", 0)

        if (selectedCategory != null) {
            val categoryPosition = adapter.getPosition(selectedCategory)
            spinnerCategories.setSelection(categoryPosition)
        }

        editTextNotificationTime.setText(notificationTime.toString())

        builder.setView(view)
            .setTitle("App settings")
            .setPositiveButton("Save") { _, _ ->
                val hideCompleted = checkBoxHideCompleted.isChecked
                val selectedCategory = spinnerCategories.selectedItem.toString()
                val notificationTime = editTextNotificationTime.text.toString().toIntOrNull() ?: 0

                val editor = sharedPreferences.edit()
                editor.putBoolean("hideCompleted", hideCompleted)
                editor.putString("selectedCategory", selectedCategory)
                editor.putInt("notificationTime", notificationTime)
                editor.apply()

                listener?.onSettingsSaved()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }
}
