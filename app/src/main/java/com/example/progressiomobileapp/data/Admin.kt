package com.example.progressiomobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "Admins",
    foreignKeys = [ForeignKey(entity = User::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"])])
data class Admin(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "admin_id")
    val adminId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "additional_admin_data")
    val additionalAdminData: String?
)