package com.example.progressiomobileapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase

class HomepageAdminActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvTodayTask: TextView
    private lateinit var tvToDoCount: TextView
    private lateinit var tvInProgressCount: TextView

    private lateinit var userDao: UserDao
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentUserEmail: String

    // Dummy data
    private val totalTasks = 10
    private val completedTasks = 2
    private val toDoTasks = 6
    private val inProgressTasks = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage_admin)

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
        val intent = Intent(this, HomepageAdminActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Task View Page
    fun goToTaskView(view: android.view.View) {
        val intent = Intent(this, TaskAdminActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Analytics Page
    fun goToAnalytics(view: android.view.View) {
        val intent = Intent(this, AnalyticsActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Calendar Page
    fun goToCalendar(view: android.view.View) {
        val intent = Intent(this, CalenderAdminActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Profile Page
    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileAdminActivity::class.java)
        startActivity(intent)
    }
}
