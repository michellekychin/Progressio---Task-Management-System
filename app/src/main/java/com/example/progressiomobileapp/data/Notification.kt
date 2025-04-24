package com.example.progressiomobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "Notifications",
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["recipient_id"]),
        ForeignKey(entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["sender_id"],
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Task::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"],
            onUpdate = ForeignKey.CASCADE)
    ])
data class Notification(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id")
    val notificationId: Int = 0,

    @ColumnInfo(name = "recipient_id")
    val recipientId: Int,

    @ColumnInfo(name = "sender_id")
    val senderId: Int?,

    @ColumnInfo(name = "task_id")
    val taskId: Int?,

    @ColumnInfo(name = "notification_type")
    val notificationType: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "is_read")
    val isRead: Int = 0
)