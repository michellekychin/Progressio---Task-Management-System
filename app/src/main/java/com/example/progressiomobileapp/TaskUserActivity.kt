package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class TaskUserActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_user)
        setupBottomNavigation(R.id.nav_tasks)
    }

    fun goToHistory(view: android.view.View) {
        val intent = Intent(this, TaskHistoryUserActivity::class.java)
        startActivity(intent)
    }

}
