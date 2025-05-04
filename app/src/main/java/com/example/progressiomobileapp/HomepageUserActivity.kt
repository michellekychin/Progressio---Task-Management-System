package com.example.progressiomobileapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.example.progressiomobileapp.data.dao.UserDao
import com.example.progressiomobileapp.data.AppDatabase

import kotlin.jvm.java
import android.Manifest
import com.example.progressiomobileapp.NotificationActivity





class HomepageUserActivity : BaseActivity() {


    private lateinit var tvGreeting: TextView
    private lateinit var tvTodayTask: TextView
    private lateinit var btnToDo: TextView
    private lateinit var btnInProgress: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var userDao: UserDao
    private lateinit var currentUserEmail: String




    // Register for permission request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        sharedPreferences.edit().putBoolean("asked_notification_permission", true).apply()
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            navigateToNotifications()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Dummy data for ToDo and In Progress
    private val toDoTasks = listOf(
        Task("Task 1", "2025-05-01"),
        Task("Task 2", "2025-05-02")
    )

    private val inProgressTasks = listOf(
        TaskWithStatus("Task 3", "In Progress", "2025-05-03"),
        TaskWithStatus("Task 4", "In Progress", "2025-05-04")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage_user)

        setupBottomNavigationUser(R.id.nav_home)

        // Initialize Views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvTodayTask = findViewById(R.id.tvTodayTask)
        btnToDo = findViewById(R.id.btnToDo)
        btnInProgress = findViewById(R.id.btnInProgress)

        // Initialize SharedPreferences and UserDao
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Retrieve user data from SharedPreferences (session data)
        val userName = sharedPreferences.getString("userName", "User") ?: "User"

        // Set the greeting message with the user's name
        tvGreeting.text = "Hello, $userName! Welcome Back!"

        // Display Today's Task count (completed/total)
        tvTodayTask.text = "2/10"  // Replace with actual data if needed

        // Set To Do tasks
        val toDoTaskDetails = toDoTasks.joinToString("\n") { "${it.title} - ${it.dueDate}" }
        btnToDo.text = toDoTaskDetails

        // Set In Progress tasks
        val inProgressTaskDetails = inProgressTasks.joinToString("\n") { "${it.title} - ${it.status} - ${it.dueDate}" }
        btnInProgress.text = inProgressTaskDetails

        // Set up the OnClickListeners to navigate to UserTaskListActivity
        btnToDo.setOnClickListener {
            navigateToTaskView("To Do")
        }

        btnInProgress.setOnClickListener {
            navigateToTaskView("In Progress")
        }
    }

    // Function to navigate to task view page
    private fun navigateToTaskView(taskType: String) {
        val intent = Intent(this, UserTaskListActivity::class.java)
        intent.putExtra("TASK_TYPE", taskType)
        startActivity(intent)
    }

    // Dummy data classes for tasks
    data class Task(val title: String, val dueDate: String)

    data class TaskWithStatus(val title: String, val status: String, val dueDate: String)


    // Navigate to Home Page
    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageUserActivity::class.java)
        startActivity(intent)
    }


    // Navigate to Notification Page
    fun goToNotifications(view: android.view.View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyAsked =
                sharedPreferences.getBoolean("asked_notification_permission", false)
            val hasPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission && !alreadyAsked) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        // Permission already granted or not needed
        navigateToNotifications()
    }

    fun navigateToNotifications() {
        val intent = Intent(this, NotificationActivity::class.java)
        startActivity(intent)
    }





    // Navigate to Task View Page
    fun goToTaskView(view: android.view.View) {

        //val intent = Intent(this, TaskUserActivity::class.java)
        //task management

        // Assuming you have a taskId to pass, here it's hardcoded as 1
        val taskId = 1 // You can fetch the actual task ID dynamically if needed

        //val intent = Intent(this, TaskDetailActivity::class.java)\
        val intent = Intent(this, UserTaskListActivity::class.java)

        //val intent = Intent(this, TaskDetailActivity::class.java)

        intent.putExtra("TASK_ID", taskId)  // Pass the taskId as an extra
        startActivity(intent)
    }

    // Navigate to Calendar Page
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
}