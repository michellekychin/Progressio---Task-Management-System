package com.example.progressiomobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "ChecklistItems",
    foreignKeys = [ForeignKey(entity = Task::class,
        parentColumns = ["task_id"],
        childColumns = ["task_id"])],
    indices = [Index("task_id")]) // Add index here
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "checklist_item_id")
    val checklistItemId: Int = 0,

    @ColumnInfo(name = "task_id")
    val taskId: Int,

    @ColumnInfo(name = "item_text")
    val itemText: String,

    @ColumnInfo(name = "is_checked")
    var isChecked: Int = 0,

    @ColumnInfo(name = "item_order")
    val itemOrder: Int?
)