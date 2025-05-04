package com.example.progressiomobileapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.dao.UserDao
import com.example.progressiomobileapp.data.dao.AdminDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ProfileUserActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvId: TextView
    private lateinit var profileImageView: ImageButton
    private lateinit var backgroundImageView: ImageButton
    private lateinit var btnLogout: Button
    private lateinit var btnLanguage: Button
    private lateinit var btnTheme: Button
    private lateinit var btnChangePassword: Button
    private lateinit var profileImageAdmin: ImageButton // Admin profile image view
    private lateinit var userDao: UserDao
    private lateinit var adminDao: AdminDao // AdminDao

    private var selectedProfileImageUri: Uri? = null
    private var selectedBackgroundImageUri: Uri? = null
    private lateinit var currentUserEmail: String
    private lateinit var currentUserGroupAdminId: String

    private val REQUEST_CODE_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)

        // Initialize Views
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvId = findViewById(R.id.tvId)
        profileImageView = findViewById(R.id.profileImage)
        backgroundImageView = findViewById(R.id.backgroundImage)
        btnLogout = findViewById(R.id.btnLogout)
        btnLanguage = findViewById(R.id.btnLanguage)
        btnTheme = findViewById(R.id.btnTheme)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        profileImageAdmin = findViewById(R.id.profileImageAdmin) // Initialize admin profile image view

        // Initialize Room Database and DAOs
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        adminDao = db.adminDao() // Get the admin DAO

        // Get the email of the logged-in user from SharedPreferences
        currentUserEmail = sharedPreferences.getString("userEmail", "") ?: ""

        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                // Set user details
                tvName.text = it.name
                tvEmail.text = it.email
                tvId.text = "ID: ${it.userId}"

                // Get groupAdminId
                currentUserGroupAdminId = it.groupAdminId.toString()

                // Set profile and background image URIs
                it.profileImageUrl?.let { uriString ->
                    selectedProfileImageUri = Uri.parse(uriString)
                    profileImageView.setImageURI(selectedProfileImageUri)
                }
                it.backgroundImageUrl?.let { uriString ->
                    selectedBackgroundImageUri = Uri.parse(uriString)
                    backgroundImageView.setImageURI(selectedBackgroundImageUri)
                }

                // Check if the user is assigned to a groupAdminId, then display the admin profile image
                if (currentUserGroupAdminId.isNotEmpty()) {
                    displayAdminProfileImage(currentUserGroupAdminId)
                }
            }
        }

        // Request permissions if not granted
        requestPermissions()

        // Handle Profile Image Click
        profileImageView.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 100) // Request code for profile image
            } else {
                requestPermissions()
            }
        }

        // Handle Background Image Click
        backgroundImageView.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 200) // Request code for background image
            } else {
                requestPermissions()
            }
        }

        // Log out functionality
        btnLogout.setOnClickListener {
            // Handle Log Out (clear session or preferences)
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish() // Close current activity
        }

        // Other Settings buttons
        btnLanguage.setOnClickListener {
            // Handle language change
            Toast.makeText(this, "Language settings", Toast.LENGTH_SHORT).show()
        }

        btnTheme.setOnClickListener {
            // Handle theme change
            Toast.makeText(this, "Theme settings", Toast.LENGTH_SHORT).show()
        }

        btnChangePassword.setOnClickListener {
            // Navigate to change password activity
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to request permissions
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    // Function to check if permissions are granted
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to display the admin profile image if the user has a groupAdminId
    private fun displayAdminProfileImage(adminId: String) {
        lifecycleScope.launch {
            val admin = adminDao.getAdminByUserId(adminId.toInt())
            admin?.let {
                // Get the admin's user ID
                val adminUserId = it.userId
                // Now fetch the admin user by userId and display their profile image
                val adminUser = userDao.getUserById(adminUserId)
                adminUser?.profileImageUrl?.let { uriString ->
                    val adminProfileUri = Uri.parse(uriString)
                    profileImageAdmin.setImageURI(adminProfileUri)
                }
            }
        }
    }

    // Handle the result from image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val imageUri: Uri = data.data!!
            when (requestCode) {
                100 -> {
                    selectedProfileImageUri = imageUri
                    profileImageView.setImageURI(imageUri)
                    saveProfileImageUri(imageUri)
                }
                200 -> {
                    selectedBackgroundImageUri = imageUri
                    backgroundImageView.setImageURI(imageUri)
                    saveBackgroundImageUri(imageUri)
                }
            }
        }
    }

    private fun saveProfileImageUri(uri: Uri) {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                val updatedUser = it.copy(profileImageUrl = uri.toString())
                userDao.update(updatedUser)
                Toast.makeText(this@ProfileUserActivity, "Profile image updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBackgroundImageUri(uri: Uri) {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                val updatedUser = it.copy(backgroundImageUrl = uri.toString())
                userDao.update(updatedUser)
                Toast.makeText(this@ProfileUserActivity, "Background image updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Navigation Functions
    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageUserActivity::class.java)
        startActivity(intent)
    }

    fun goToTaskView(view: android.view.View) {
        val intent = Intent(this, UserTaskListActivity::class.java)
        startActivity(intent)
    }

    fun goToCalendar(view: android.view.View) {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }

    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileUserActivity::class.java)
        startActivity(intent)
    }
}
