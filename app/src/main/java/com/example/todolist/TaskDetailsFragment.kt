package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.todolist.entities.Task

class TaskDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Odbierz przekazane szczegóły zadania
        val taskId = arguments?.getLong("TASK_ID") ?: -1L
        if (taskId != -1L) {
            // Pobierz zadanie z bazy danych lub innego źródła danych
            //val task = getTaskFromDatabase(taskId)

            // Wyświetl szczegóły zadania w interfejsie użytkownika
            //displayTaskDetails(task)
        }
    }

//    private fun getTaskFromDatabase(taskId: Long): Task {
//        // Pobierz zadanie z bazy danych
//    }

    private fun displayTaskDetails(task: Task) {
        // Wyświetl szczegóły zadania w interfejsie użytkownika
    }
}
