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
import kotlinx.coroutines.flow.collect



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

        // Start observing the comments from ViewModel
        lifecycleScope.launch {
            // Fetch comments when the activity starts
            viewModel.getCommentsForTask(taskId)

            // Correct usage of collect without extra arguments
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

        // Back Button Logic: Initialize and set click listener for the back button
        binding.backButton.setOnClickListener {
            val userRole = getUserRole()  // Get the user's role

            val intent = when (userRole) {
                "admin" -> Intent(this, HomepageAdminActivity::class.java) // Redirect to HomepageAdminActivity if admin
                else -> Intent(this, HomepageUserActivity::class.java) // Redirect to HomepageUserActivity if user
            }

            startActivity(intent)
            finish() // Close the current activity
        }
    }

    // Function to get the user's role from SharedPreferences
    private fun getUserRole(): String {
        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("userRole", "user") ?: "user"
    }
}
