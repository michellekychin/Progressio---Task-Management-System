package com.example.progressiomobileapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progressiomobileapp.data.dao.TaskDao

class TaskViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(taskDao) as T // Return the ViewModel directly without CreationExtras
    }
}
