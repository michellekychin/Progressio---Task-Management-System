package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class HomepageUserActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvTodayTask: TextView
    private lateinit var tvToDoCount: TextView
    private lateinit var tvInProgressCount: TextView

    private val userName = "Ling"  // Fetch this dynamically after login
    private val totalTasks = 20
    private val completedTasks = 5
    private val toDoTasks = 20
    private val inProgressTasks = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage_user)



        // Initialize Views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvTodayTask = findViewById(R.id.tvTodayTask)
        tvToDoCount = findViewById(R.id.tvToDoCount)
        tvInProgressCount = findViewById(R.id.tvInProgressCount)

        // Set the greeting message
        tvGreeting.text = "Hello, $userName! Welcome Back!"

        // Display Today's Task count (completed/total)
        tvTodayTask.text = "$completedTasks/$totalTasks"

        // Display To Do tasks count
        tvToDoCount.text = "$toDoTasks"

        // Display In Progress tasks count
        tvInProgressCount.text = "$inProgressTasks"
    }

    // Navigate to Notification Page
    fun goToNotifications(view: android.view.View) {
        // Example: startActivity(Intent(this, NotificationActivity::class.java))
        val intent = Intent(this, NotificationActivity::class.java)
        startActivity(intent)
    }

    // Navigate to To Do Tasks Page
    fun openToDoTasks(view: android.view.View) {
        // Example: startActivity(Intent(this, ToDoTasksActivity::class.java))
    }

    // Navigate to In Progress Tasks Page
    fun openInProgressTasks(view: android.view.View) {
        // Example: startActivity(Intent(this, InProgressTasksActivity::class.java))
    }

    // Navigate to Task View Page
    fun goToTaskView(view: android.view.View) {
        // Example: startActivity(Intent(this, TaskViewActivity::class.java))
    }

    // Navigate to Calendar Page
    fun goToCalendar(view: android.view.View) {
        // Example: startActivity(Intent(this, CalendarActivity::class.java))
    }

    // Navigate to Profile Page
    fun goToProfile(view: android.view.View) {
        // Example: startActivity(Intent(this, ProfileActivity::class.java))
    }
}
