package com.example.progressiomobileapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.dao.UserDao

class TaskViewModelFactory(private val taskDao: TaskDao,  private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(taskDao, userDao) as T // Return the ViewModel directly without CreationExtras
    }
}
