package com.example.progressiomobileapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.Notification
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.data.User
import com.example.progressiomobileapp.data.dao.NotificationDao
import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.dao.UserDao
import com.example.progressiomobileapp.data.Admin
import com.example.progressiomobileapp.data.dao.AdminDao
import com.example.progressiomobileapp.databinding.ActivityNotificationBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.content.Intent
import androidx.appcompat.widget.AppCompatImageButton
import com.example.progressiomobileapp.HomepageAdminActivity
import com.example.progressiomobileapp.HomepageUserActivity
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AlertDialog
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.res.stringResource

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationDao: NotificationDao
    private lateinit var recyclerViewAdapter: NotificationAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao
    private lateinit var adminDao: AdminDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show a test notification when the user enters the activity
        NotificationUtils.showNotification(
            this,
            getString(R.string.notification_test_title),
            getString(R.string.notification_test_body)
        )

        askNotificationPermissionIfNeeded()


        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val tabLayout = binding.tabLayout


        notificationDao = AppDatabase.getDatabase(applicationContext).notificationDao()
        userDao = AppDatabase.getDatabase(applicationContext).userDao()
        taskDao = AppDatabase.getDatabase(applicationContext).taskDao()
        adminDao = AppDatabase.getDatabase(applicationContext).adminDao()

        // Insert fake data if necessary for testing
        lifecycleScope.launch {
            insertFakeData() // Insert fake data when the activity starts
        }

        // Setup the back button in the toolbar

        val backButton: AppCompatImageButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            // Go back to the previous activity
            onBackPressed()  // This will navigate back to the previous activity in the stack
        }


        // Initialize TabLayout
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_all)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_unread)))

        // Load initial data for "All"
        loadNotifications("All")

        // Handle tab selection to update RecyclerView
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadNotifications("All") // Load "All" notifications
                    1 -> loadNotifications("Unread") // Load "Unread" notifications
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Handle "Mark All as Read" button click
        val markAllAsReadButton = binding.markAllAsReadButton
        markAllAsReadButton.setOnClickListener {
            lifecycleScope.launch {
                // Retrieve the logged-in user's email from SharedPreferences
                val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("userEmail", null)

                if (userEmail != null) {
                    // User is logged in, proceed with the rest of the logic
                    lifecycleScope.launch {
                        val user = userDao.getUserByEmail(userEmail)
                        user?.userId?.let { userId ->
                            // Continue with marking notifications as read logic
                            val dontAskAgain =
                                sharedPreferences.getBoolean("dontAskAgain_user_$userId", false)

                            if (dontAskAgain) {
                                markAllNotificationsAsRead()  // Directly mark all as read without asking
                            } else {
                                showMarkAllAsReadDialog(
                                    sharedPreferences,
                                    userId
                                )  // Show confirmation dialog for the user
                            }
                        }
                    }
                } else {
                    // User is not logged in, handle the case accordingly (e.g., show login screen)
                    Toast.makeText(
                        this@NotificationActivity,
                        "User not logged in.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

        private fun showMarkAllAsReadDialog(sharedPreferences: SharedPreferences, userId: Int) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_mark_all_as_read, null)
        val checkBox = view.findViewById<CheckBox>(R.id.dontAskAgainCheckbox)

        // Check if the user has already chosen "Don't ask me again"
        val dontAskAgainForUser = sharedPreferences.getBoolean("dontAskAgain_user_$userId", false)
        checkBox.isChecked = dontAskAgainForUser

        builder.setTitle(getString(R.string.dialog_mark_all_title))
            .setMessage(getString(R.string.dialog_mark_all_message))
            .setView(view)  // Inflate the custom layout with the checkbox
            .setPositiveButton(getString(R.string.mark_all_as_read)) { _, _ ->
                if (checkBox.isChecked) {
                    // Save the decision to SharedPreferences for this user
                    sharedPreferences.edit().putBoolean("dontAskAgain_user_$userId", true).apply()
                }
                markAllNotificationsAsRead()  // Mark notifications as read
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()  // Dismiss the dialog without doing anything
            }
            .show()
    }



    // Function to mark all notifications as read
    private fun markAllNotificationsAsRead() {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail("nat@gmail.com")
            user?.userId?.let { userId ->
                notificationDao.markAllAsRead(userId)
                Toast.makeText(
                    this@NotificationActivity,
                    "All notifications marked as read",
                    Toast.LENGTH_SHORT
                ).show()
                loadNotifications("All")  // Reload notifications after marking them as read
            }
        }
    }

    // Function to show a test notification
    private fun showTestNotification() {
        NotificationUtils.showNotification(
            this,
            "Test Notification",
            "This is a test notification to ensure notifications are working."
        )
    }

    // Function to load notifications based on the selected tab ("All" or "Unread")
    private fun loadNotifications(type: String) {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail("nat@gmail.com")
            val userId = user?.userId ?: return@launch

            val notifications = if (type == "Unread") {
                notificationDao.getUnreadNotifications(userId)  // Fetch unread notifications
            } else {
                notificationDao.getNotificationsForRecipient(userId)  // Fetch all notifications
            }

            notifications.collect { data ->
                Log.d("NotificationActivity", "Loaded ${data.size} notifications for type: $type")

                // Initialize the RecyclerView adapter with the new data
                recyclerViewAdapter = NotificationAdapter(data) { notification ->
                    val intent = Intent(this@NotificationActivity, TaskDetailActivity::class.java)
                    intent.putExtra("TASK_ID", notification.taskId)
                    startActivity(intent)
                }
                recyclerView.adapter = recyclerViewAdapter
                recyclerViewAdapter.notifyDataSetChanged() // Notify the adapter of data changes
            }
        }
    }


    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(
                this,
                getString(R.string.toast_notifications_enabled),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.toast_notifications_disabled),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun askNotificationPermissionIfNeeded() {
        val prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val asked = prefs.getBoolean("asked_notification", false)

        // Check if the user has chosen "Don't ask me again"
        val dontAskAgain = prefs.getBoolean("dontAskAgain", false)

        if (!dontAskAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        100
                    )
                }
            }
        }
    }


    private fun insertFakeData() {
        lifecycleScope.launch {
            // Collect all users from the database using Flow
            val usersFlow = userDao.getAllUsers()

            // Collect the users from the Flow
            usersFlow.collect { allUsers ->
                if (allUsers.isNotEmpty()) {
                    Log.d("NotificationActivity", "Found ${allUsers.size} users in the database.")

                    // Insert 3 tasks for each existing user
                    allUsers.forEach { user ->
                        for (i in 1..3) {
                            val task = Task(
                                title = "Task for ${user.name} - $i",
                                description = "Description for Task ${user.name} - $i",
                                status = "To-Do",
                                dueDate = "2025-05-0$i",
                                assignedTo = user.userId, // Use the current user's ID
                                createdBy = user.userId,  // Task creator is the user themselves for simplicity
                                creationDate = "2025-04-28",
                                historyId = null
                            )
                            taskDao.insert(task) // Insert the task
                            Log.d("NotificationActivity", "Inserted task for ${user.name}: Task $i")
                        }
                    }

                    // Insert notifications for each user
                    allUsers.forEach { user ->
                        val notifications = listOf(
                            Notification(
                                recipientId = user.userId,
                                senderId = user.userId,  // Sender is the same user for testing
                                taskId = 1,
                                notificationType = "Comment",
                                message = "Commented on Task 1",
                                createdAt = "2025-04-28",
                                isRead = 1 // Read notification
                            ),
                            Notification(
                                recipientId = user.userId,
                                senderId = user.userId,  // Sender is the same user for testing
                                taskId = 2,
                                notificationType = "Comment",
                                message = "Commented on Task 2",
                                createdAt = "2025-04-28",
                                isRead = 0 // Unread notification
                            ),
                            Notification(
                                recipientId = user.userId,
                                senderId = user.userId,  // Sender is the same user for testing
                                taskId = 3,
                                notificationType = "Comment",
                                message = "Commented on Task 3",
                                createdAt = "2025-04-28",
                                isRead = 1 // Read notification
                            )
                        )

                        // Insert notifications for this user
                        notifications.forEach { notification ->
                            notificationDao.insert(notification)
                            Log.d("NotificationActivity", "Inserted notification for ${user.name}: Task ${notification.taskId}")
                        }
                    }

                    Log.d("NotificationActivity", "Fake tasks and notifications inserted for all users!")
                } else {
                    Log.d("NotificationActivity", "No users found. Skipping dummy data insertion.")
                }
            }
        }
    }

}