package com.example.progressiomobileapp



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.Comment
import com.example.progressiomobileapp.data.ChecklistItem
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.data.dao.CommentDao
import com.example.progressiomobileapp.data.dao.ChecklistItemDao
import com.example.progressiomobileapp.data.dao.TaskDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



//class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

//    private val taskDao = AppDatabase.getDatabase(application).taskDao()
//    private val checklistItemDao = AppDatabase.getDatabase(application).checklistItemDao()
//    private val commentDao = AppDatabase.getDatabase(application).commentDao()

    // Fetch task details using Flow
//    fun getTaskDetails(taskId: Int): Flow<Task?> {
//        return flow {
//            emit(taskDao.getTaskById(taskId)) // Emit the result of the suspend function
//        }
//    }

    // Fetch checklist items using Flow
//    fun getChecklistItemsForTask(taskId: Int): Flow<List<ChecklistItem>> {
//        return checklistItemDao.getChecklistItemsForTask(taskId) // Return Flow<List<ChecklistItem>>
//    }

    // Fetch comments using Flow
//   fun getCommentsForTask(taskId: Int): Flow<List<Comment>> {
//       return commentDao.getCommentsForTask(taskId) // Return Flow<List<Comment>>
//    }

    // Insert a new comment (this will update the database)
//    fun addComment(comment: Comment) {
//        viewModelScope.launch {
//            commentDao.insert(comment)
//        }
//    }
//}
// before adding dummy data


class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val checklistItemDao = AppDatabase.getDatabase(application).checklistItemDao()
    private val commentDao = AppDatabase.getDatabase(application).commentDao()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> get() = _comments

    // Assume this is the logged-in user's email (you can get this from SharedPreferences or another method)
    private val loggedInUserEmail = "nat@gmail.com"

    // Get the task details (returning dummy data if logged-in user is 'nat@gmail.com')
    fun getTaskDetails(taskId: Int): Flow<Task> {
        return if (loggedInUserEmail == "nat@gmail.com") {
            // Return dummy data if user is 'nat@gmail.com'
            flowOf(getDummyTask())
        } else {
            // Fetch real data from the database for other users
            flow {
                val task = taskDao.getTaskById(taskId) // Real data fetch
                if (task != null) {
                    emit(task) // Emit real task data
                } else {
                    emit(getDummyTask()) // Emit dummy data if task not found
                }
            }
        }
    }

    // Get checklist items (returning dummy data if logged-in user is 'nat@gmail.com')
    fun getChecklistItemsForTask(taskId: Int): Flow<List<ChecklistItem>> {
        return if (loggedInUserEmail == "nat@gmail.com") {
            flowOf(getDummyChecklistItems(taskId)) // Dummy checklist items
        } else {
            checklistItemDao.getChecklistItemsForTask(taskId) // Real data fetch
        }
    }

    fun getCommentsForTask(taskId: Int) {
        viewModelScope.launch {
            // If the logged-in user is 'nat@gmail.com', emit dummy comments
            if (loggedInUserEmail == "nat@gmail.com") {
                // Emit dummy comments directly
                _comments.value = getDummyComments(taskId)
            } else {
                // Fetch real comments from the database for other users
                commentDao.getCommentsForTask(taskId).collect { comments ->
                    // Emit the fetched comments to _comments
                    _comments.value = comments
                }
            }
        }
    }


    // Add a comment to the database
    fun addComment(comment: Comment) {
        viewModelScope.launch {
            commentDao.insert(comment)

            // After insertion, collect updated comments and emit them to _comments
            commentDao.getCommentsForTask(comment.taskId).collect { updatedComments ->
                _comments.value = updatedComments
            }
        }
    }


    // Dummy task data for testing
    private fun getDummyTask(): Task {
        return Task(
            taskId = 1,
            title = "Test Task",
            description = "This is a test task assigned to nat@gmail.com.",
            status = "To-Do",
            dueDate = "2025-05-01",
            assignedTo = 1, // This would normally be the userId of nat@gmail.com
            createdBy = 1, // Assuming the task is created by the admin (user ID 1)
            creationDate = System.currentTimeMillis().toString(),
            completionDate = null,
            pendingReviewTime = null,
            historyId = null
        )
    }

    // Dummy checklist items for testing
    private fun getDummyChecklistItems(taskId: Int): List<ChecklistItem> {
        return listOf(
            ChecklistItem(
                checklistItemId = 1,
                taskId = taskId,
                itemText = "Check Item 1",
                isChecked = 0,
                itemOrder = 1
            ),
            ChecklistItem(
                checklistItemId = 2,
                taskId = taskId,
                itemText = "Check Item 2",
                isChecked = 0,
                itemOrder = 2
            ),
            ChecklistItem(
                checklistItemId = 3,
                taskId = taskId,
                itemText = "Check Item 3",
                isChecked = 0,
                itemOrder = 3
            )
        )
    }

    // Dummy comments for testing
    private fun getDummyComments(taskId: Int): List<Comment> {
        return listOf(
            Comment(
                commentId = 1,
                taskId = taskId,
                userId = 1, // Assuming nat@gmail.com has userId 1
                commentText = "This is a comment for the task.",
                createdAt = System.currentTimeMillis().toString()
            ),
            Comment(
                commentId = 2,
                taskId = taskId,
                userId = 1, // Assuming nat@gmail.com has userId 1
                commentText = "Another comment for testing.",
                createdAt = System.currentTimeMillis().toString()
            )
        )
    }

}



