package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.todolist.entities.Task

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
        taskTitle = view.findViewById(R.id.taskTitle)
        description = view.findViewById(R.id.taskDescription)
        taskCreatedAt = view.findViewById(R.id.taskCreatedAt)
        taskDueDate = view.findViewById(R.id.taskDueDate)
        taskStatus = view.findViewById(R.id.taskStatus)
        taskNotificationEnabled = view.findViewById(R.id.taskNotificationEnabled)
        taskCategory = view.findViewById(R.id.taskCategory)

        taskTitle.text = task.title
        description.text = task.description
        taskCreatedAt.text = task.createdAt.toString()
        taskDueDate.text = task.dueDate.toString()
        taskStatus.text = task.status.toString()
        taskNotificationEnabled.text = task.notificationEnabled.toString()
        taskCategory.text = task.category

        // Odbierz przekazane szczegóły zadania
//        val taskId = arguments?.getLong("TASK_ID") ?: -1L
//        if (taskId != -1L) {
//            viewModel.getTask(taskId).observe(viewLifecycleOwner) { task ->
//                if (task != null) {
//                    displayTaskDetails(task)
//                } else {
//                    // Handle the case where the task is not found (e.g., show a message)
//                    Toast.makeText(requireContext(), "Task not found", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }


    }

//    private fun getTaskFromDatabase(taskId: Long): Task {
//        // Pobierz zadanie z bazy danych
//    }
//
//    private fun displayTaskDetails(task: Task) {
//        // Wyświetl szczegóły zadania w interfejsie użytkownika
//    }
}
