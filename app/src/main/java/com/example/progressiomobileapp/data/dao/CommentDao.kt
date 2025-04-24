package com.example.progressiomobileapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.Comment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: Comment): Long

    @Query("SELECT * FROM Comments WHERE task_id = :taskId")
    fun getCommentsForTask(taskId: Int): Flow<List<Comment>>

    @Query("SELECT * FROM Comments WHERE user_id = :userId")
    fun getCommentsByUser(userId: Int): Flow<List<Comment>>

    @Update
    suspend fun update(comment: Comment)

    @Delete
    suspend fun delete(comment: Comment)

    @Query("SELECT * FROM Comments")
    fun getAllComments(): Flow<List<Comment>>
}