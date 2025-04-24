package com.example.progressiomobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(tableName = "History",
    foreignKeys = [
        ForeignKey(entity = Task::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"]),
        ForeignKey(entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"])
    ])
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "history_id")
    val historyId: Int = 0,

    @ColumnInfo(name = "task_id")
    val taskId: Int,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "action")
    val action: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "changed_at")
    val changedAt: String,

    @ColumnInfo(name = "old_value")
    val oldValue: String?,

    @ColumnInfo(name = "new_value")
    val newValue: String?
)