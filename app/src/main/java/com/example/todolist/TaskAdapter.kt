package com.example.todolist;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

                val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
                val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val task = taskList[position]
                holder.titleTextView.text = task.title
                holder.descriptionTextView.text = task.description
        }

        override fun getItemCount(): Int {
                return taskList.size
        }

        fun addTask(task: Task) {
                taskList.add(task)
                notifyDataSetChanged()
        }
}
