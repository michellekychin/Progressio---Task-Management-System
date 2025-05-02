package com.example.progressiomobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.Task

class TaskAdapter(
    private val tasks: List<Task>,
    private val onClick: (String) -> Unit // Click listener for passing taskId
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder class using View directly
    inner class TaskViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.taskTitle)
        val dueDate: TextView = view.findViewById(R.id.taskDueDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.dueDate.text = "Due Date: ${task.dueDate}"

        // Set the click listener
        holder.view.setOnClickListener {
            onClick(task.taskId.toString()) // Call the click listener with the taskId
        }
    }

    override fun getItemCount(): Int = tasks.size
}
