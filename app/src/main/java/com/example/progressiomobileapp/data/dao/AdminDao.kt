package com.example.progressiomobileapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.Admin

@Dao
interface AdminDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(admin: Admin): Long

    @Query("SELECT * FROM Admins WHERE admin_id = :adminId")
    suspend fun getAdminById(adminId: Int): Admin?

    @Query("SELECT * FROM Admins WHERE user_id = :userId")
    suspend fun getAdminByUserId(userId: Int): Admin?

    @Update
    suspend fun update(admin: Admin)

    @Delete
    suspend fun delete(admin: Admin)

    @Query("SELECT * FROM Admins")
    fun getAllAdmins(): Flow<List<Admin>>
}