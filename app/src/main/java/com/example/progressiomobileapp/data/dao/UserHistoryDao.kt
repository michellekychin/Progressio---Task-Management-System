package com.example.progressiomobileapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.UserHistory

@Dao
interface UserHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userHistory: UserHistory): Long

    @Query("SELECT * FROM UserHistory WHERE user_id = :userId")
    fun getUserHistoryForUser(userId: Int): Flow<List<UserHistory>>

    @Query("SELECT * FROM UserHistory WHERE history_id = :historyId")
    fun getUserHistoryForHistory(historyId: Int): Flow<List<UserHistory>>

    @Update
    suspend fun update(userHistory: UserHistory)

    @Delete
    suspend fun delete(userHistory: UserHistory)

    @Query("SELECT * FROM UserHistory")
    fun getAllUserHistory(): Flow<List<UserHistory>>
}