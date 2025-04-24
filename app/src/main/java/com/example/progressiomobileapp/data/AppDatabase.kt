package com.example.progressiomobileapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.progressiomobileapp.data.dao.*

@Database(entities = [User::class, Admin::class, Task::class, ChecklistItem::class, Comment::class, History::class, UserHistory::class, Notification::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun adminDao(): AdminDao
    abstract fun taskDao(): TaskDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun commentDao(): CommentDao
    abstract fun historyDao(): HistoryDao
    abstract fun userHistoryDao(): UserHistoryDao
    abstract fun notificationDao(): NotificationDao
}