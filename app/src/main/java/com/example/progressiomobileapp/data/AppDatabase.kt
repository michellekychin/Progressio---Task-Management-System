package com.example.progressiomobileapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.progressiomobileapp.data.dao.*


// Define the database class
@Database(entities = [User::class, Admin::class, Task::class, ChecklistItem::class, Comment::class, History::class, UserHistory::class, Notification::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Accessor method for UserDao
    abstract fun userDao(): UserDao

    // Accessor method for AdminDao
    abstract fun adminDao(): AdminDao

    // Accessor method for TaskDao
    abstract fun taskDao(): TaskDao

    // Accessor method for ChecklistItemDao
    abstract fun checklistItemDao(): ChecklistItemDao

    // Accessor method for CommentDao
    abstract fun commentDao(): CommentDao

    // Accessor method for HistoryDao
    abstract fun historyDao(): HistoryDao

    // Accessor method for UserHistoryDao
    abstract fun userHistoryDao(): UserHistoryDao

    // Accessor method for NotificationDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Explicitly define the type of MIGRATION_1_2 as Migration
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop the table if it exists
                database.execSQL("DROP TABLE IF EXISTS Task")

                // Recreate the Task table
                database.execSQL(""" 
                    CREATE TABLE IF NOT EXISTS `Task` (
                        `task_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT,
                        `status` TEXT DEFAULT 'To-Do',
                        `due_date` TEXT,
                        `assigned_to` INTEGER NOT NULL,
                        `created_by` INTEGER NOT NULL,
                        `creation_date` TEXT NOT NULL,
                        `completion_date` TEXT,
                        `pending_review_time` TEXT,
                        `history_id` INTEGER DEFAULT NULL,
                        FOREIGN KEY(`assigned_to`) REFERENCES `User`(`user_id`),
                        FOREIGN KEY(`created_by`) REFERENCES `Admin`(`admin_id`)
                    )
                """)
            }
        }

        // Singleton pattern to get a single instance of the database
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"  // Name of your database
                )
                    .fallbackToDestructiveMigration()  // Automatically resets the database if migrations are not found
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.execSQL("PRAGMA foreign_keys=OFF;")
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}