package com.example.progressiomobileapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.data.dao.TaskDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.TaskViewModelFactory
import kotlinx.coroutines.flow.Flow

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    // Fetch task by ID with callback
    fun getTaskById(taskId: Int, onTaskFetched: (Task?) -> Unit) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            onTaskFetched(task)
        }
    }

    fun getTasksForDate(date: String): Flow<List<Task>> {
        return taskDao.getTasksForDate(date)
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }
}
