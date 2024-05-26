package com.example.todolist;

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.entities.Task

class TaskAdapter(private var taskList: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private var onDeleteClickListener: ((Task) -> Unit)? = null
    private var onItemClickListener: ((Task) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (Task) -> Unit) {
        onDeleteClickListener = listener
    }

    fun setOnItemClickListener(listener: (Task) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(taskList[position])
                }
            }
        }
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
            showConfirmationDialog(holder.itemView.context, task, position)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun updateTasks(newList: List<Task>) {
        taskList = newList
        notifyDataSetChanged()
    }


    private fun showConfirmationDialog(context: Context, task: Task, position: Int) {
        val dialog = AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                onDeleteClickListener?.invoke(task)
                notifyItemRemoved(position)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}
