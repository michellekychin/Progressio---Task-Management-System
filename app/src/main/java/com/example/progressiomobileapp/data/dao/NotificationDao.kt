package com.example.progressiomobileapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.Notification

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: Notification): Long

    @Query("SELECT * FROM Notifications WHERE recipient_id = :recipientId")
    fun getNotificationsForRecipient(recipientId: Int): Flow<List<Notification>>

    @Query("SELECT * FROM Notifications WHERE sender_id = :senderId")
    fun getNotificationsFromSender(senderId: Int): Flow<List<Notification>>

    @Query("SELECT * FROM Notifications WHERE task_id = :taskId")
    fun getNotificationsForTask(taskId: Int): Flow<List<Notification>>

    @Update
    suspend fun update(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)

    @Query("SELECT * FROM Notifications")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("UPDATE Notifications SET is_read = 1 WHERE recipient_id = :recipientId")
    suspend fun markAllAsRead(recipientId: Int)

    @Query("SELECT * FROM Notifications WHERE recipient_id = :recipientId AND is_read = 0")
    fun getUnreadNotifications(recipientId: Int): Flow<List<Notification>>

    @Query("SELECT * FROM Notifications WHERE sender_id = :adminId OR task_id IN (SELECT task_id FROM Tasks WHERE created_by = :adminId)")
    fun getNotificationsForAdmin(adminId: Int): Flow<List<Notification>>


}
