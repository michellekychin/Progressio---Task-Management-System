package com.example.progressiomobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(tableName = "UserHistory",
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"]),
        ForeignKey(entity = History::class,
            parentColumns = ["history_id"],
            childColumns = ["history_id"])
    ])
data class UserHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_history_id")
    val userHistoryId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "history_id")
    val historyId: Int
)