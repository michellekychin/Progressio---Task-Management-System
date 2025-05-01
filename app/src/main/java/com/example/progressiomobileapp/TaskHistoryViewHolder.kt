package com.example.progressiomobileapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val taskTitle: TextView = view.findViewById(R.id.tvTaskTitle)
    val taskStatus: TextView = view.findViewById(R.id.tvTaskStatus)
    val taskDueDate: TextView = view.findViewById(R.id.tvTaskDueDate)  // Added for due date
}
