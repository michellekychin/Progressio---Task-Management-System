package com.example.progressiomobileapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.data.dao.TaskDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.TaskViewModelFactory
import com.example.progressiomobileapp.data.User
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.flow.map

class TaskViewModel(private val taskDao: TaskDao, private val userDao: UserDao) : ViewModel() {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksForUser(userId: Int, isAdmin: Boolean): Flow<List<Task>> {
        return if (isAdmin) {
            allTasks
        } else {
            allTasks.map { tasks ->
                tasks.filter { it.assignedTo == userId } // ✅ correct property name
            }
        }
    }



    // Fetch task by ID with callback
    fun getTaskById(taskId: Int, onTaskFetched: (Task?) -> Unit) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            onTaskFetched(task)
        }
    }

    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userDao.getUserByEmail(email)
            callback(user)
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


    // Fetch user by ID and return the user role
    fun getUserRole(userId: Int, onUserRoleFetched: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userDao.getUserById(userId)
            onUserRoleFetched(user)
        }
    }


}
