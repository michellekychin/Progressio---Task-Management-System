package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.Task

class UserTaskListActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao
    private lateinit var userDao: UserDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_task_list)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskDao = AppDatabase.getDatabase(applicationContext).taskDao()
        userDao = AppDatabase.getDatabase(applicationContext).userDao()

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


