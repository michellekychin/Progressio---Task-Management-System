package com.example.progressiomobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    fun setupBottomNavigation(currentId: Int) {
        val nav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (nav != null) {
            nav.selectedItemId = currentId
            nav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> startActivity(Intent(this, HomepageAdminActivity::class.java))
                    R.id.nav_tasks -> startActivity(Intent(this, TaskAdminActivity::class.java))
                    R.id.nav_analytics -> startActivity(Intent(this, AnalyticsActivity::class.java))
                    R.id.nav_schedule -> startActivity(Intent(this, CalendarActivity::class.java))
                    R.id.nav_profile -> startActivity(Intent(this, ProfileAdminActivity::class.java))
                }
                finish()
                overridePendingTransition(0, 0) // No animation
                true
            }
        } else {
            // Optional: Log a warning
            android.util.Log.w("BaseActivity", "BottomNavigationView not found in layout.")
        }
    }

    fun setupBottomNavigationUser(currentId: Int) {
        val nav = findViewById<BottomNavigationView>(R.id.bottom_navigation_user)
        if (nav != null) {
            nav.selectedItemId = currentId
            nav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> startActivity(Intent(this, HomepageUserActivity::class.java))
                    R.id.nav_tasks -> startActivity(Intent(this, UserTaskListActivity::class.java))
                    R.id.nav_schedule -> startActivity(Intent(this, CalendarActivity::class.java))
                    R.id.nav_profile -> startActivity(Intent(this, ProfileUserActivity::class.java))
                }
                finish()
                overridePendingTransition(0, 0)
                true
            }
        } else {
            android.util.Log.w("BaseActivity", "BottomNavigationView (user) not found in layout.")
        }
    }
}
