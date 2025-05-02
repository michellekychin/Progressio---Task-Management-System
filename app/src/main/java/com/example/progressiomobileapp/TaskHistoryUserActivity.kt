package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.progressiomobileapp.data.Task

class TaskHistoryUserActivity : AppCompatActivity() {

    private lateinit var taskListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_history_user)

        // Initialize the container for task list
        taskListContainer = findViewById(R.id.taskListContainer)

        // Directly display dummy data (no need for database or DAO)
        displayDummyData()
    }

    // Function to display hardcoded dummy data directly
    private fun displayDummyData() {
        val taskList = listOf(
            Task(
                title = "Create a software",
                description = "Complete the software project",
                status = "100%",
                dueDate = "2025-05-01",
                assignedTo = 1,  // Dummy value for assignedTo
                createdBy = 1,   // Dummy value for createdBy
                creationDate = "2025-04-01", // Dummy value for creationDate
                historyId = 1    // Dummy value for historyId
            ),
            Task(
                title = "Debug",
                description = "Fix bugs in the project",
                status = "100%",
                dueDate = "2025-04-30",
                assignedTo = 2,  // Dummy value for assignedTo
                createdBy = 1,   // Dummy value for createdBy
                creationDate = "2025-04-10", // Dummy value for creationDate
                historyId = 2    // Dummy value for historyId
            )
        )

        // Clear any existing views
        taskListContainer.removeAllViews()

        // Add each task to the LinearLayout dynamically
        taskList.forEach { task ->
            val taskView = createTaskView(task)  // Create individual task view using item_task_history.xml
            taskListContainer.addView(taskView)  // Add task view to container
        }
    }

    // Create a view for each task dynamically using item_task_history.xml layout
    private fun createTaskView(task: Task): LinearLayout {
        // Inflate the item_task_history layout
        val taskView = LayoutInflater.from(this).inflate(R.layout.item_task_history, taskListContainer, false)

        // Find the TextViews inside item_task_history.xml and set the data
        val taskTitle = taskView.findViewById<TextView>(R.id.tvTaskTitle)
        val taskStatus = taskView.findViewById<TextView>(R.id.tvTaskStatus)
        val taskDueDate = taskView.findViewById<TextView>(R.id.tvTaskDueDate)

        // Set task data to TextViews
        taskTitle.text = task.title
        taskStatus.text = task.status
        taskDueDate.text = task.dueDate

        // Set click listener to navigate to TaskDetailActivity
        taskView.setOnClickListener {
            val intent = Intent(this, TaskDetailUserActivity::class.java)
            intent.putExtra("TASK_TITLE", task.title)
            intent.putExtra("TASK_STATUS", task.status)
            intent.putExtra("TASK_DUE_DATE", task.dueDate)
            intent.putExtra("TASK_DESCRIPTION", task.description)
            startActivity(intent)
        }

        return taskView as LinearLayout
    }
}
