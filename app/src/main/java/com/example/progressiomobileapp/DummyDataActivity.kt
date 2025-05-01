package com.example.progressiomobileapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.progressiomobileapp.data.User
import com.example.progressiomobileapp.data.Admin
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.data.dao.UserDao
import com.example.progressiomobileapp.data.dao.AdminDao
import com.example.progressiomobileapp.data.dao.TaskDao
import kotlinx.coroutines.launch
import android.util.Log
import com.example.progressiomobileapp.data.AppDatabase

class DummyDataActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao
    private lateinit var adminDao: AdminDao
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room Database and DAOs
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "task_database"
        ).build()

        // Get DAO instances
        userDao = db.userDao()
        adminDao = db.adminDao()
        taskDao = db.taskDao()

        // Insert dummy data in background thread
        lifecycleScope.launch {
            insertDummyData()
        }
    }

    private suspend fun insertDummyData() {
        // Insert User data
        val user = User(name = "John Doe", email = "john.doe@example.com", password = "password123", role = "User")
        val userId = userDao.insert(user)
        Log.d("DummyData", "Inserted User ID: $userId")

        // Insert Admin data
        val admin = Admin(userId = userId.toInt(), additionalAdminData = "Admin for task management")
        val adminId = adminDao.insert(admin)
        Log.d("DummyData", "Inserted Admin ID: $adminId")

        // Insert Task data
        val task1 = Task(
            title = "Create a software",
            description = "Complete the software project",
            status = "100%",
            dueDate = "2025-05-01",
            assignedTo = userId.toInt(),
            createdBy = adminId.toInt(),
            creationDate = "2025-04-20",
            historyId = 1
        )

        val task2 = Task(
            title = "Debug",
            description = "Fix bugs in the project",
            status = "100%",
            dueDate = "2025-04-30",
            assignedTo = userId.toInt(),
            createdBy = adminId.toInt(),
            creationDate = "2025-04-15",
            historyId = 2
        )

        // Insert tasks into the database
        taskDao.insert(task1)
        taskDao.insert(task2)

        Log.d("DummyData", "Inserted Tasks: ${task1.title}, ${task2.title}")
    }
}
