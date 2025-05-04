package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.Task

class TaskHistoryUserActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskHistoryAdapter: TaskHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_history_user)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTaskHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Call function to load task data
        displayDummyData()
    }

    // Function to display hardcoded dummy data directly
    private fun displayDummyData() {
        val taskList = listOf(
            Task(
                title = "Create a software",
                description = """
                • Complete the software project
                • Develop user interface
                • Integrate backend APIs
                • Ensure smooth user experience
                • Perform testing and debugging
                • Deploy software for final review
            """.trimIndent(),
                status = "100%",
                dueDate = "2025-05-01",
                assignedTo = 1,  // Dummy value for assignedTo
                createdBy = 1,   // Dummy value for createdBy
                creationDate = "2025-04-01", // Dummy value for creationDate
                historyId = 1    // Dummy value for historyId
            ),
            Task(
                title = "Debug",
                description = """
                • Fix bugs in the project
                • Test the software after debugging
                • Identify and resolve memory leaks
                • Improve performance of the app
                • Re-test all modules after fixes
            """.trimIndent(),
                status = "100%",
                dueDate = "2025-04-30",
                assignedTo = 2,  // Dummy value for assignedTo
                createdBy = 1,   // Dummy value for createdBy
                creationDate = "2025-04-10", // Dummy value for creationDate
                historyId = 2    // Dummy value for historyId
            ),
            Task(
                title = "Write documentation",
                description = """
                • Write documentation for the software project
                • Document system architecture
                • Include user manual for each module
                • Write troubleshooting guide
                • Create API documentation
            """.trimIndent(),
                status = "50%",
                dueDate = "2025-05-10",
                assignedTo = 3,  // Dummy value for assignedTo
                createdBy = 1,   // Dummy value for createdBy
                creationDate = "2025-04-12", // Dummy value for creationDate
                historyId = 3    // Dummy value for historyId
            ),
            Task(
                title = "Test software",
                description = """
                • Conduct testing on the software project
                • Perform unit testing for all modules
                • Run integration tests
                • Test the user interface on multiple devices
                • Ensure compatibility with major browsers
            """.trimIndent(),
                status = "25%",
                dueDate = "2025-05-15",
                assignedTo = 4,  // Dummy value for assignedTo
                createdBy = 1,   // Dummy value for createdBy
                creationDate = "2025-04-15", // Dummy value for creationDate
                historyId = 4    // Dummy value for historyId
            )
        )

        // Set the adapter with the list of tasks
        taskHistoryAdapter = TaskHistoryAdapter(taskList) { task ->
            // When a task is clicked, navigate to the TaskDetailUserActivity with the task details
            val intent = Intent(this, TaskDetailUserActivity::class.java)
            intent.putExtra("TASK_TITLE", task.title)
            intent.putExtra("TASK_STATUS", task.status)
            intent.putExtra("TASK_DUE_DATE", task.dueDate)
            intent.putExtra("TASK_DESCRIPTION", task.description)
            startActivity(intent)
        }
        recyclerView.adapter = taskHistoryAdapter
    }

    fun goToTaskView(view: android.view.View) {
        val intent = Intent(this, UserTaskListActivity::class.java)
        startActivity(intent)
    }
}