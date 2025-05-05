package com.example.progressiomobileapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.adapter.TaskAdapter
import com.example.progressiomobileapp.data.*
import com.example.progressiomobileapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskAdminActivity : BaseActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var btnAddTask: ImageButton

    private val dummyUsers = listOf(
        User(2, "John Doe", "john@example.com", "password123", "user", 1),
        User(3, "Jane Smith", "jane@example.com", "password123", "user", 1),
        User(4, "Emily Johnson", "emily@example.com", "password123", "user", 1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_admin)
        setupBottomNavigation(R.id.nav_tasks)

        taskRecyclerView = findViewById(R.id.recyclerViewTasks)
        btnAddTask = findViewById(R.id.btnAddTask)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        taskAdapter = TaskAdapter(
            emptyList(),
            onEditClick = { showTaskDialog(it) },
            onDeleteClick = { showDeleteConfirmation(it) }
        )

        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        lifecycleScope.launch {
            taskViewModel.tasks.collect { tasks ->
                taskAdapter.updateTasks(tasks)
            }
        }

        btnAddTask.setOnClickListener {
            showTaskDialog(null)
        }

        insertInitialUserAndAdmin()
    }

    fun goToHistory(view: android.view.View) {
        val intent = Intent(this, TaskHistoryAdminActivity::class.java)
        startActivity(intent)
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_task))
            .setMessage(getString(R.string.delete_task_confirmation))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                taskViewModel.deleteTask(task)
                showMessage(true, getString(R.string.delete))
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showMessage(success: Boolean, type: String) {
        val msg = when {
            success && type == getString(R.string.create) -> getString(R.string.task_created_successfully)
            !success && type == getString(R.string.create)  -> getString(R.string.task_creation_failed)
            success && type == getString(R.string.update)  -> getString(R.string.task_updated)
            !success && type == getString(R.string.update)  -> getString(R.string.task_update_failed)
            success && type == getString(R.string.delete) -> getString(R.string.task_deleted)
            else -> getString(R.string.task_deletion_failed)
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    suspend fun validateUserExists(userId: Int): Boolean {
        val user = taskViewModel.getUserById(userId)
        return user != null
    }

    private fun showTaskDialog(existingTask: Task?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (existingTask == null) getString(R.string.add_task) else getString(R.string.edit_task))

        val view = layoutInflater.inflate(R.layout.dialog_task_form, null)
        val etTitle = view.findViewById<EditText>(R.id.etTaskTitle)
        val etDescription = view.findViewById<EditText>(R.id.etTaskDescription)
        val etDueDate = view.findViewById<EditText>(R.id.etTaskDueDate)
        val checkboxChecklist = view.findViewById<CheckBox>(R.id.checkboxChecklist)
        val checklistContainer = view.findViewById<LinearLayout>(R.id.checklistContainer)
        val btnAssign = view.findViewById<ImageView>(R.id.btnAssign)
        val tvAssign = view.findViewById<TextView>(R.id.tvAssign)
        val etChecklistItem = view.findViewById<EditText>(R.id.etChecklistItem)
        val btnAddChecklistItem = view.findViewById<Button>(R.id.btnAddChecklistItem)
        val checklistItemContainer = view.findViewById<LinearLayout>(R.id.checklistItemContainer)

        var selectedUserId: Int? = null
        val checklistItems = mutableListOf<CheckBox>()

        if (existingTask != null) {
            etTitle.setText(existingTask.title)
            etDescription.setText(existingTask.description)
            etDueDate.setText(existingTask.dueDate)

            selectedUserId = existingTask.assignedTo
            tvAssign.text = selectedUserId?.toString() ?: getString(R.string.no_user_selected) // Display the user ID (assigned_to)

            lifecycleScope.launch {
                taskViewModel.getChecklistItemsByTaskId(existingTask.taskId).collect { items ->
                    items.forEach { item ->
                        val checkBox = CheckBox(this@TaskAdminActivity).apply {
                            text = item.itemText
                            isChecked = item.isChecked == 1
                        }
                        checklistItemContainer.addView(checkBox)
                        checklistItems.add(checkBox)
                    }
                }
            }
        }

        etDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val selected = Calendar.getInstance().apply { set(year, month, day) }
                etDueDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selected.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        checkboxChecklist.setOnCheckedChangeListener { _, isChecked ->
            checklistContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        btnAddChecklistItem.setOnClickListener {
            val itemText = etChecklistItem.text.toString()
            if (itemText.isNotEmpty()) {
                val checkBox = CheckBox(this).apply { text = itemText }
                checklistItemContainer.addView(checkBox)
                checklistItems.add(checkBox)
                etChecklistItem.text.clear()
            }
        }

        btnAssign.setOnClickListener {
            val userIds = dummyUsers.map { it.userId.toString() }.toTypedArray() // List of user IDs
            val selectedIndex = dummyUsers.indexOfFirst { it.userId == selectedUserId }

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.assign_task))
                .setSingleChoiceItems(userIds, selectedIndex) { dialog, which ->
                    selectedUserId = dummyUsers[which].userId // Assign selected user's ID to the task
                }
                .setPositiveButton(getString(R.string.done)) { _, _ ->
                    tvAssign.text = selectedUserId?.toString() ?: getString(R.string.no_user_selected) // Display the user ID (assigned_to)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }

        builder.setView(view)
        builder.setPositiveButton(if (existingTask == null) getString(R.string.create) else getString(R.string.update)) { _, _ ->
            val task = Task(
                taskId = existingTask?.taskId ?: 0, // Use existing task ID if editing
                title = etTitle.text.toString(),
                description = etDescription.text.toString(),
                status = existingTask?.status ?: getString(R.string.to_do),
                dueDate = etDueDate.text.toString(),
                assignedTo = selectedUserId ?: existingTask?.assignedTo ?: 1, // Set assignedTo as user ID
                createdBy = 1,
                creationDate = existingTask?.creationDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                completionDate = existingTask?.completionDate,
                pendingReviewTime = existingTask?.pendingReviewTime,
                historyId = existingTask?.historyId
            )

            lifecycleScope.launch {
                try {
                    // Ensure the assigned user exists
                    if (selectedUserId != null) {
                        val isUserValid = validateUserExists(task.assignedTo)
                        if (isUserValid) {
                            val taskId = if (existingTask == null) {
                                taskViewModel.addTaskAndReturnId(task)
                            } else {
                                taskViewModel.updateTask(task)
                                task.taskId
                            }

                            taskViewModel.deleteChecklistItemsByTaskId(taskId.toInt()) // Ensure previous checklist is cleared

                            checklistItems.forEachIndexed { index, checkBox ->
                                val item = ChecklistItem(
                                    taskId = taskId.toInt(),
                                    itemText = checkBox.text.toString(),
                                    isChecked = if (checkBox.isChecked) 1 else 0,
                                    itemOrder = index
                                )
                                taskViewModel.addChecklistItem(item)
                            }

                            showMessage(true, if (existingTask == null) getString(R.string.create) else getString(R.string.update))
                        } else {
                            // If user is invalid, assign to a default valid user (e.g., user_id = 1)
                            task.assignedTo = 1 // Default user
                            taskViewModel.addTask(task)
                            showMessage(false, getString(R.string.create))
                            Toast.makeText(this@TaskAdminActivity,
                                getString(R.string.invalid_user_id), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showMessage(false, getString(R.string.create))
                        Toast.makeText(this@TaskAdminActivity,
                            getString(R.string.invalid_user), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage(false, if (existingTask == null) getString(R.string.create) else getString(
                        R.string.update
                    ))
                }
            }
        }

        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.show()
    }

    private fun insertInitialUserAndAdmin() {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val existingUser = db.userDao().getUserById(1)
                val existingAdmin = db.adminDao().getAdminByUserId(1)

                if (existingUser == null) {
                    db.userDao().insert(
                        User(1, "Michelle", "michellekychin@gmail.com", "Cky@040314", "admin", 1)
                    )
                }

                if (existingAdmin == null) {
                    db.adminDao().insert(Admin(1, 1, null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@TaskAdminActivity, "Failed to insert user/admin", Toast.LENGTH_LONG).show()
            }
        }
    }
}
