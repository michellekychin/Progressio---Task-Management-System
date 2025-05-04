package com.example.progressiomobileapp.data

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log

class DummyDataSeederActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()
        val adminDao = db.adminDao()
        val taskDao = db.taskDao()

        lifecycleScope.launch {
            // Step 1: Create User for admin
            var adminUser = userDao.getUserByEmail("admin@gmail.com")
            var adminUserId: Int

            if (adminUser == null) {
                val newAdminUser = User(
                    name = "admin1",
                    email = "admin@gmail.com",
                    password = "ABcd123$",
                    role = "admin"
                )
                val insertedId = userDao.insert(newAdminUser)
                adminUserId = insertedId.toInt()
                adminUser = userDao.getUserById(adminUserId)!!
            } else {
                adminUserId = adminUser.userId
            }

            // Step 2: Link that User to an Admin account
            var admin = adminDao.getAdminByUserId(adminUserId)
            val adminId = if (admin == null) {
                val newAdmin = Admin(
                    userId = adminUserId,
                    adminId = 2,
                    additionalAdminData = TODO()
                )
                adminDao.insert(newAdmin).toInt()
            } else {
                admin.adminId
            }

            // Step 3: Create regular user
            var user = userDao.getUserByEmail("nat@gmail.com")
            var userId = 2

            if (user == null) {
                val newUser = User(
                    name = "nat",
                    email = "nat@gmail.com",
                    password = "ABcd123$",
                    role = "user"
                )
                val insertedId = userDao.insert(newUser)
                userId = insertedId.toInt()
            } else {
                userId = user.userId
            }

            // Step 4: Assign a task from admin to the user
            val task = Task(
                title = "Dummy Task from Admin",
                description = "This task is assigned to nat by admin1.",
                dueDate = "2025-05-10",
                assignedTo = userId,
                createdBy = adminId,
                creationDate = System.currentTimeMillis().toString(),
                historyId = null
            )
            Log.d("Seeder", "Running DummyDataSeederActivity")


            taskDao.insert(task)

        }
    }
}
