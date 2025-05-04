package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.Task

class UserTaskListActivity : BaseActivity() {

    private lateinit var taskDao: TaskDao
    private lateinit var userDao: UserDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_task_list)

        // Set up the bottom navigation
        setupBottomNavigationUser(R.id.nav_tasks)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)




        taskDao = AppDatabase.getDatabase(applicationContext).taskDao()
        userDao = AppDatabase.getDatabase(applicationContext).userDao()

        displayDummyData()




        val userId = getSharedPreferences("userPrefs", MODE_PRIVATE).getInt("userId", 0)
        Log.d("TaskDetailActivity", "User ID entered activity: $userId")


        lifecycleScope.launch {
            val user = userDao.getUserByEmail("nat@gmail.com") // Replace with actual login
            val userId = user?.userId ?: return@launch

            val tasks = taskDao.getTasksAssignedToUser(userId)
            tasks.collect { taskList ->
                adapter = TaskAdapter(taskList) { taskId ->
                    // When a task is clicked, pass the task ID to TaskDetailActivity
                    val intent = Intent(this@UserTaskListActivity, TaskDetailActivity::class.java)
                    intent.putExtra("TASK_ID", taskId)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        }
    }

    private fun displayDummyData() {
        val taskList = listOf(
            Task(
                title = "Create a software",
                description = "Complete the software project",
                status = "100%",
                dueDate = "2025-05-01",
                assignedTo = 1,
                createdBy = 1,
                creationDate = "2025-04-01",
                historyId = 1
            ),
            Task(
                title = "Debug",
                description = "Fix bugs in the project",
                status = "100%",
                dueDate = "2025-04-30",
                assignedTo = 2,
                createdBy = 1,
                creationDate = "2025-04-10",
                historyId = 2
            )
        )

        adapter = TaskAdapter(taskList) { taskId ->
            val intent = Intent(this@UserTaskListActivity, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", taskId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageUserActivity::class.java)
        startActivity(intent)
    }

    fun goToCalendar(view: android.view.View) {
        // Create an Intent to navigate to CalendarActivity
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent) // Start the activity

    }


    // Navigate to Profile Page
    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileUserActivity::class.java)
        startActivity(intent)
    }




    fun goToHistory(view: android.view.View) {
        val intent = Intent(this, TaskHistoryUserActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Home Page
    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageUserActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Task View Page
    fun goToTaskView(view: android.view.View) {
        val intent = Intent(this, UserTaskListActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Calendar Page
    /*fun goToCalendar(view: android.view.View) {
        val intent = Intent(this, CalendarAdminActivity::class.java)
        startActivity(intent)
    }*/

    // Navigate to Profile Page
    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileUserActivity::class.java)
        startActivity(intent)
    }

}


