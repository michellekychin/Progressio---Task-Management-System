package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.example.progressiomobileapp.MainCalendarScreen
import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.TaskViewModel
import com.example.progressiomobileapp.data.dao.AdminDao
import com.example.progressiomobileapp.data.dao.UserDao
import com.example.progressiomobileapp.data.Task
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CalendarActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Retrieve user info from SharedPreferences
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", null)
        val userRole = sharedPref.getString("userRole", null)
        val userName = sharedPref.getString("userName", null)

        if (userEmail == null || userRole == null || userName == null) {
            Log.e("CalendarActivity", "Missing user data in SharedPreferences")
            return
        }

        Log.d("CalendarActivity", "User info - Email: $userEmail, Name: $userName, Role: $userRole")

        // Initialize DAOs and ViewModel
        val db = AppDatabase.getDatabase(applicationContext)
        val taskDao: TaskDao = db.taskDao()
        val userDao: UserDao = db.userDao()
        val adminDao: AdminDao = db.adminDao()
        val taskViewModel = ViewModelProvider(this, TaskViewModelFactory(taskDao, userDao)).get(TaskViewModel::class.java)

        // Fetch user by email (which is unique)
        taskViewModel.getUserByEmail(userEmail) { user ->
            if (user != null) {
                val isAdmin = user.role.trim().equals("admin", ignoreCase = true)
                Log.d("CalendarActivity", "Loaded user: ${user.name}, isAdmin = $isAdmin")

                // Set the content of the activity with the appropriate layout for Admin or User
                if (isAdmin) {
                    setContentView(R.layout.activity_calendar_admin) // Use the admin layout
                    // Setup the admin bottom navigation
                    setupBottomNavigation(R.id.nav_schedule)
                } else {
                    setContentView(R.layout.activity_calendar) // Use the user layout
                    // Setup the user bottom navigation
                    setupBottomNavigationUser(R.id.nav_schedule)
                }

                // Create dummy data for tasks
                val dummyTasks = listOf(
                    Task(
                        title = "Task 1",
                        description = "Complete the project",
                        status = "To-Do",
                        dueDate = "2025-05-01",
                        assignedTo = user.userId,
                        createdBy = user.userId,
                        creationDate = "2025-04-01",
                        historyId = null
                    ),
                    Task(
                        title = "Task 2",
                        description = "Fix bugs in the system",
                        status = "In Progress",
                        dueDate = "2025-05-10",
                        assignedTo = user.userId,
                        createdBy = user.userId,
                        creationDate = "2025-04-05",
                        historyId = null
                    ),
                    Task(
                        title = "Task 3",
                        description = "Prepare for the meeting",
                        status = "Completed",
                        dueDate = "2025-05-15",
                        assignedTo = user.userId,
                        createdBy = user.userId,
                        creationDate = "2025-04-10",
                        historyId = null
                    )
                )

                // Insert dummy tasks into the database
                lifecycleScope.launch {
                    dummyTasks.forEach { taskDao.insert(it) }
                    Log.d("CalendarActivity", "Inserted dummy tasks into the database.")
                }


                // Set the content of the activity with the MainCalendarScreen composable
                val composeView = findViewById<ComposeView>(R.id.calendar_content)
                composeView.setContent {
                    MainCalendarScreen(
                        userId = user.userId,
                        isAdmin = isAdmin,
                        adminDao = adminDao,
                        taskViewModel = taskViewModel
                    )
                }

            } else {
                Log.e("CalendarActivity", "User not found for email $userEmail")
            }
        }



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
    fun goToCalendar(view: android.view.View) {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Profile Page
    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileUserActivity::class.java)
        startActivity(intent)
    }

}
