package com.example.progressiomobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "Comments",
    foreignKeys = [
        ForeignKey(entity = Task::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"]),
        ForeignKey(entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"])
    ])
data class Comment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "comment_id")
    val commentId: Int = 0,

    @ColumnInfo(name = "task_id")
    val taskId: Int,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "comment_text")
    val commentText: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String
)