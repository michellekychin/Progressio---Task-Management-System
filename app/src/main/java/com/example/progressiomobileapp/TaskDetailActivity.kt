package com.example.progressiomobileapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progressiomobileapp.data.Comment
import com.example.progressiomobileapp.data.ChecklistItem
import com.example.progressiomobileapp.databinding.ActivityTaskDetailBinding
import kotlinx.coroutines.launch
import android.util.Log
import android.content.Intent
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collect
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.Notification
import com.example.progressiomobileapp.ChecklistItemAdapter
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.data.Admin
import com.example.progressiomobileapp.data.User



class TaskDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private val viewModel: TaskDetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val taskId = intent.getIntExtra("TASK_ID", 0)

        // Initialize the comment adapter
        val commentAdapter = CommentAdapter()
        binding.commentsRecyclerView.adapter = commentAdapter

        // Set up RecyclerViews
        binding.checklistRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.submitTaskButton.setOnClickListener {
            Log.d("SubmitTask", "Submit button clicked")  // <-- Add this line

            lifecycleScope.launch {
                val task = viewModel.getTaskById(taskId)  // Fetch the task using the taskId
                Log.d("SubmitTask", "Retrieved task: $task")
                val userId = getSharedPreferences("userPrefs", MODE_PRIVATE).getInt("userId", 0)

                if (task != null) {
                    // Mark task as completed
                    task.status = "Completed"
                    viewModel.updateTask(task)

                    // Create a notification to be sent to the admin
                    val notification = Notification(
                        recipientId = task.createdBy,  // Admin user
                        senderId = userId,             // Regular user
                        taskId = task.taskId,
                        notificationType = "Task Completed",
                        message = "The task '${task.title}' has been marked as completed by the user.",
                        createdAt = System.currentTimeMillis().toString(),
                        isRead = 0
                    )

                    // Insert notification into the database
                    viewModel.insertNotification(notification)

                    Log.d("SubmitTask", "Task submitted and notification sent to admin.")
                    finish()  // Close the activity
                }
            }
        }



        // Start observing the comments from ViewModel
        lifecycleScope.launch {
            viewModel.getCommentsForTask(taskId)

            viewModel.comments.collect { comments ->
                commentAdapter.submitList(comments)  // Update RecyclerView with the comments
            }
        }

        // Fetch task data from ViewModel using collect
        lifecycleScope.launch {
            viewModel.getTaskDetails(taskId).collect { task ->
                binding.taskTitle.text = task.title
                binding.taskDescription.text = task.description
            }
        }

        // Fetch checklist items using collect
        lifecycleScope.launch {
            viewModel.getChecklistItemsForTask(taskId).collect { checklistItems ->
                binding.checklistRecyclerView.adapter = ChecklistItemAdapter(checklistItems)
                checkChecklistCompletion(checklistItems) // Check completion every time the list updates
            }
        }

        // Add Comment Button Logic
        binding.addCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                val comment = Comment(
                    taskId = taskId,
                    userId = 1, // Use actual user ID
                    commentText = commentText,
                    createdAt = System.currentTimeMillis().toString()
                )

                // Log the comment being added
                Log.d("TaskDetailActivity", "Adding comment: $commentText")

                viewModel.addComment(comment)
            }
        }

        // Back Button Logic
        binding.backButton.setOnClickListener {
            val userRole = getUserRole()  // Get the user's role

            val intent = when (userRole) {
                "admin" -> Intent(this, HomepageAdminActivity::class.java)
                else -> Intent(this, HomepageUserActivity::class.java)
            }

            startActivity(intent)
            finish() // Close the current activity
        }
    }


    private fun getUserRole(): String {
        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("userRole", "user") ?: "user"
    }


    private fun sendCompletionNotificationToAdmin(item: ChecklistItem) {
        lifecycleScope.launch {
            // Get the task to retrieve admin (creator) ID
            val task = viewModel.getTaskById(item.taskId)
            Log.d("SubmitTask", "Retrieved task: $task")
            val adminId = task?.createdBy ?: return@launch
            val userId = getSharedPreferences("userPrefs", MODE_PRIVATE).getInt("userId", 0)
            Log.d("TaskDetailActivity", "User ID entered activity: $userId")


            val notification = Notification(
                recipientId = adminId,
                senderId = userId,
                taskId = item.taskId,
                notificationType = "Completion",
                message = "Checklist item '${item.itemText}' completed by user.",
                createdAt = System.currentTimeMillis().toString(),
                isRead = 0
            )

            viewModel.insertNotification(notification)

            Log.d("TaskDetailActivity", "Notification sent to admin: ${notification.message}")
        }
    }


    // Function to check if all checklist items are completed after 5 minutes
    private fun checkChecklistCompletion(checklistItems: List<ChecklistItem>) {
        val currentTime = System.currentTimeMillis()
        checklistItems.forEach { item ->
            if (item.isChecked == 1 && item.checkedTimestamp != null) {
                val timeElapsed = currentTime - item.checkedTimestamp
                if (timeElapsed >= 5 * 60 * 1000) { // 5 minutes in milliseconds
                    updateChecklistItemCompleted(item)
                }
            }
        }
    }

    // Function to update the checklist item as completed
    private fun updateChecklistItemCompleted(item: ChecklistItem) {
        item.isChecked = 2 // Mark as completed
        lifecycleScope.launch {
            viewModel.updateChecklistItem(item)
            sendCompletionNotificationToAdmin(item) // Send notification to admin
        }
    }

}





