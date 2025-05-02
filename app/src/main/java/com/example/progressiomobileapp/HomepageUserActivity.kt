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
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase
import kotlin.jvm.java
import android.Manifest
import com.example.progressiomobileapp.NotificationActivity



class HomepageUserActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvTodayTask: TextView
    private lateinit var tvToDoCount: TextView
    private lateinit var tvInProgressCount: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var userDao: UserDao
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentUserEmail: String

    // Dummy data
    private val totalTasks = 10
    private val completedTasks = 2
    private val toDoTasks = 6
    private val inProgressTasks = 2



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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage_user)

        // Initialize Views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvTodayTask = findViewById(R.id.tvTodayTask)
        tvToDoCount = findViewById(R.id.tvToDoCount)
        tvInProgressCount = findViewById(R.id.tvInProgressCount)

        // Initialize SharedPreferences and UserDao
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Get the email of the logged-in user from SharedPreferences
        currentUserEmail = sharedPreferences.getString("userEmail", "") ?: ""

        // Fetch the user's data from the database using email
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                // Set the greeting message with the user's name
                tvGreeting.text = "Hello, ${it.name}! Welcome Back!"
            }
        }

        // Display Today's Task count (completed/total)
        tvTodayTask.text = "$completedTasks/$totalTasks"

        // Display To Do tasks count
        tvToDoCount.text = "$toDoTasks"

        // Display In Progress tasks count
        tvInProgressCount.text = "$inProgressTasks"
    }


    // Navigate to Home Page
    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageUserActivity::class.java)
        startActivity(intent)

    // Navigate to Notification Page
    fun goToNotifications(view: android.view.View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyAsked = sharedPreferences.getBoolean("asked_notification_permission", false)
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

    private fun navigateToNotifications() {
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



