package com.example.progressiomobileapp.data.dao


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History): Long

    @Query("SELECT * FROM History WHERE task_id = :taskId")
    fun getHistoryForTask(taskId: Int): Flow<List<History>>

    @Query("SELECT * FROM History WHERE user_id = :userId")
    fun getHistoryForUser(userId: Int): Flow<List<History>>

    @Update
    suspend fun update(history: History)

    @Delete
    suspend fun delete(history: History)

    @Query("SELECT * FROM History")
    fun getAllHistory(): Flow<List<History>>
}