package com.example.progressiomobileapp

import com.example.progressiomobileapp.data.dao.AdminDao
import com.example.progressiomobileapp.data.dao.UserDao
import com.example.progressiomobileapp.data.Admin
import com.example.progressiomobileapp.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UserRoleManager {

    // Function to check if the user is an admin based on the Admin table
    suspend fun isAdmin(userId: Int, adminDao: AdminDao): Boolean {
        val admin = getAdminByUserId(userId, adminDao)
        return admin != null
    }

    // Helper function to get admin by userId
    private suspend fun getAdminByUserId(userId: Int, adminDao: AdminDao): Admin? {
        return withContext(Dispatchers.IO) {
            adminDao.getAdminByUserId(userId)
        }
    }

    // Function to get user by ID (optional, in case you need more user info)
    suspend fun getUserById(userId: Int, userDao: UserDao): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }
}
