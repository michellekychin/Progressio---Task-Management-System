package com.example.progressiomobileapp

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class HomepageAdminActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvSubmittedTask: TextView
    private lateinit var btnNeedReview: TextView  // Change to TextView

    private lateinit var sharedPreferences: SharedPreferences

    // Dummy data for tasks that need review
    private val tasksNeedReview = listOf(
        Task("Task 1", "2025-05-01"),
        Task("Task 2", "2025-05-02")
    )

    // Dummy data for task counts (this will change dynamically based on actual data)
    private val totalTasks = 40
    private val completedTasks = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage_admin)

        // Initialize views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvSubmittedTask = findViewById(R.id.tvSubmittedTask)
        btnNeedReview = findViewById(R.id.btnNeedReview)  // Find the TextView here

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        // Retrieve user data from SharedPreferences (session data)
        val userName = sharedPreferences.getString("userName", "User") ?: "User"

        // Set the greeting message with the admin user's name
        tvGreeting.text = "Hello, $userName! Welcome Back!"

        // Display Submitted Task count (completed/total)
        tvSubmittedTask.text = "$completedTasks/$totalTasks - ${calculateProgress(completedTasks, totalTasks)}"

        // Display task titles and due dates in the TextView (Need of Review section)
        val taskDetails = tasksNeedReview.joinToString("\n") { "${it.title} - ${it.dueDate}" }
        btnNeedReview.text = taskDetails

        // Set up the TextView click listener to navigate to the task review page
        btnNeedReview.setOnClickListener {
            val intent = Intent(this, TaskAdminActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to calculate task progress percentage
    private fun calculateProgress(completed: Int, total: Int): String {
        return if (total > 0) {
            val progress = (completed.toDouble() / total.toDouble()) * 100
            String.format("%.2f", progress) + "%"
        } else {
            "0%"
        }
    }

    // Define the Task class (for task data)
    data class Task(val title: String, val dueDate: String)

    // Other navigation functions
    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageAdminActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Notification Page
    fun goToNotifications(view: android.view.View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyAsked =
                sharedPreferences.getBoolean("asked_notification_permission", false)
            val hasPermission = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission && !alreadyAsked) {
                val requestPermissionLauncher = null
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



    fun goToTaskView(view: android.view.View) {
        val intent = Intent(this, TaskAdminActivity::class.java)
        startActivity(intent)
    }

    fun goToAnalytics(view: android.view.View) {
        val intent = Intent(this, AnalyticsActivity::class.java)
        startActivity(intent)
    }

    fun goToCalendar(view: android.view.View) {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }

    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileAdminActivity::class.java)
        startActivity(intent)
    }
}

private fun Nothing?.launch(string: String) {}
