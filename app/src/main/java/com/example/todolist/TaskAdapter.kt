package com.example.todolist;

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.entities.Task

class TaskAdapter(private val taskList: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

        private var onDeleteClickListener: ((Task) -> Unit)? = null

        fun setOnDeleteClickListener(listener: (Task) -> Unit) {
                onDeleteClickListener = listener
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

                val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
                val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
                val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val task = taskList[position]
                holder.titleTextView.text = task.title
                holder.descriptionTextView.text = task.description
                holder.deleteButton.setOnClickListener {
                        showConfirmationDialog(holder.itemView.context, task, position)                }
        }

        override fun getItemCount(): Int {
                return taskList.size
        }

        fun updateTasks(newList: List<Task>) {
                taskList.clear()
                taskList.addAll(newList)
                notifyDataSetChanged()
        }

        fun addTask(task: Task) {
                taskList.add(task)
                notifyDataSetChanged()
        }

        private fun showConfirmationDialog(context: Context, task: Task, position: Int) {
                val dialog = AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Delete") { _, _ ->
                                onDeleteClickListener?.invoke(task) // Call listener if set, passing task
                                taskList.removeAt(position) // Remove task from local list after confirmation
                                notifyItemRemoved(position) // Notify adapter about item removal
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                        }
                        .create()
                dialog.show()
        }
}
