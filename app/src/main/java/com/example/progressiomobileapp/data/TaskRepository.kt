package com.example.progressiomobileapp.data


import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.dao.NotificationDao
import com.example.progressiomobileapp.data.Task

class TaskRepository(
    private val taskDao: TaskDao,
    private val notificationDao: NotificationDao
) {


    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

}
