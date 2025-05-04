package com.example.progressiomobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.Task
import android.widget.TextView

class TaskHistoryAdapter(
    private val taskList: List<Task>,
    private val onItemClickListener: (Task) -> Unit
) : RecyclerView.Adapter<TaskHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_history, parent, false)
        return TaskHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskHistoryViewHolder, position: Int) {
        val task = taskList[position]

        // Set task data to the corresponding TextViews
        holder.taskTitle.text = task.title
        holder.taskStatus.text = "${task.status} Tasks"  // You can format this as per your needs
        holder.taskDueDate.text = task.dueDate  // Display the due date instead of description

        // Set click listener for task item
        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(task)
        }
    }

    override fun getItemCount(): Int = taskList.size
}
