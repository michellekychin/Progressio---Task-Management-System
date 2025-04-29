package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class AnalyticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)



        // Navigate to Home Page
        fun goToHome(view: android.view.View) {
            val intent = Intent(this, HomepageUserActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Task View Page
        fun goToTaskView(view: android.view.View) {
            val intent = Intent(this, TaskUserActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Calendar Page
        fun goToCalendar(view: android.view.View) {
            val intent = Intent(this, CalenderUserActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Profile Page
        fun goToProfile(view: android.view.View) {
            val intent = Intent(this, ProfileUserActivity::class.java)
            startActivity(intent)
        }
    }
}