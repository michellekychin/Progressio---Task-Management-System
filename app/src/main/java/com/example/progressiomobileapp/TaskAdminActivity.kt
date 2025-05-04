package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class TaskAdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_admin)


    }

    fun goToHistory(view: android.view.View) {
        val intent = Intent(this, TaskHistoryAdminActivity::class.java)
        startActivity(intent)
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
    /*fun goToCalendar(view: android.view.View) {
        val intent = Intent(this, CalendarAdminActivity::class.java)
        startActivity(intent)
    }*/

    // Navigate to Profile Page
    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileAdminActivity::class.java)
        startActivity(intent)
    }
}