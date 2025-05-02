package com.example.progressiomobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.progressiomobileapp.MainCalendarScreen
import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.TaskViewModel

class CalendarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize TaskDao from AppDatabase
        val taskDao: TaskDao = AppDatabase.getDatabase(applicationContext).taskDao()

        // Initialize TaskViewModel with TaskDao
        val taskViewModel = ViewModelProvider(this, TaskViewModelFactory(taskDao)).get(TaskViewModel::class.java)

        // Set the content of the activity with the MainCalendarScreen composable
        setContent {
            MainCalendarScreen(taskViewModel = taskViewModel) // Pass the ViewModel
        }
    }
}
