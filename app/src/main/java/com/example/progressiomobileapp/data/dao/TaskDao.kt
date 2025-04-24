package com.example.progressiomobileapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.Task


@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Query("SELECT * FROM Tasks WHERE task_id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM Tasks WHERE assigned_to = :assignedTo")
    fun getTasksAssignedToUser(assignedTo: Int): Flow<List<Task>>

    @Query("SELECT * FROM Tasks WHERE created_by = :createdBy")
    fun getTasksCreatedByAdmin(createdBy: Int): Flow<List<Task>>

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM Tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM Tasks WHERE due_date BETWEEN :startDate AND :endDate")
    fun getTasksBetweenDates(startDate: String, endDate: String): Flow<List<Task>>

    @Query("SELECT * FROM Tasks WHERE strftime('%Y-%m-%d', due_date) = :date")
    fun getTasksForDate(date: String): Flow<List<Task>>
}

