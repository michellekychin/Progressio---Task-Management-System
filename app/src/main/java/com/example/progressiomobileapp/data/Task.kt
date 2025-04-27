package com.example.progressiomobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "Tasks",
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["assigned_to"]),
        ForeignKey(entity = Admin::class,
            parentColumns = ["admin_id"],
            childColumns = ["created_by"])
    ],
    indices = [Index(value = ["assigned_to"]), Index(value = ["created_by"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    val taskId: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "status")
    val status: String = "To-Do", // Default status is "To-Do"

    @ColumnInfo(name = "due_date")
    val dueDate: String?,

    @ColumnInfo(name = "assigned_to")
    val assignedTo: Int,

    @ColumnInfo(name = "created_by")
    val createdBy: Int,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "completion_date")
    val completionDate: String? = null,

    @ColumnInfo(name = "pending_review_time")
    val pendingReviewTime: String? = null,

    @ColumnInfo(name = "history_id")
    val historyId: Int?
)