package com.example.progressiomobileapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailUserActivity : AppCompatActivity() {

    private lateinit var taskTitleTextView: TextView
    private lateinit var taskStatusTextView: TextView
    private lateinit var taskDueDateTextView: TextView
    private lateinit var taskDescriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail_user)

        // Initialize the TextViews
        taskTitleTextView = findViewById(R.id.tvTaskTitle)
        taskStatusTextView = findViewById(R.id.tvTaskStatus)
        taskDueDateTextView = findViewById(R.id.tvTaskDueDate)
        taskDescriptionTextView = findViewById(R.id.tvTaskDescription)

        // Get the task data passed from the Intent
        val taskTitle = intent.getStringExtra("TASK_TITLE")
        val taskStatus = intent.getStringExtra("TASK_STATUS")
        val taskDueDate = intent.getStringExtra("TASK_DUE_DATE")
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION")

        // Set the data to the TextViews
        taskTitleTextView.text = taskTitle
        taskStatusTextView.text = taskStatus
        taskDueDateTextView.text = taskDueDate
        taskDescriptionTextView.text = taskDescription
    }
}
