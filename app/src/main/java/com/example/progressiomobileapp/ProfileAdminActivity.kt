package com.example.progressiomobileapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvId: TextView
    private lateinit var profileImageView: ImageButton
    private lateinit var backgroundImageView: ImageButton
    private lateinit var btnLogout: Button
    private lateinit var btnLanguage: Button
    private lateinit var btnTheme: Button
    private lateinit var btnChangePassword: Button
    private lateinit var edtEmail: EditText
    private lateinit var btnAddUser: Button
    private lateinit var emailInputLayout: LinearLayout
    private lateinit var btnSubmitEmail: Button
    private lateinit var userDao: UserDao

    private var selectedProfileImageUri: Uri? = null
    private var selectedBackgroundImageUri: Uri? = null
    private lateinit var currentUserEmail: String

    companion object {
        const val REQUEST_CODE_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_admin)

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
        edtEmail = findViewById(R.id.edtEmail)
        btnAddUser = findViewById(R.id.btnAddUser)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        btnSubmitEmail = findViewById(R.id.btnSubmitEmail)

        // Initialize Room Database and UserDao
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val db = AppDatabase.getDatabase(this)

        userDao = db.userDao()

        // Get the email of the logged-in user from SharedPreferences
        currentUserEmail = sharedPreferences.getString("userEmail", "") ?: ""
        Log.d("ProfileAdminActivity", "Current User Email: $currentUserEmail")

        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                // For admin only: Set user details (name, email, id)
                if (it.role.toLowerCase() == "admin") {
                    tvName.text = it.name
                    tvEmail.text = it.email
                    tvId.text = "ID: ${it.userId}"

                    // Set profile and background image URIs
                    it.profileImageUrl?.let { uriString ->
                        selectedProfileImageUri = Uri.parse(uriString)
                        profileImageView.setImageURI(selectedProfileImageUri)
                    }
                    it.backgroundImageUrl?.let { uriString ->
                        selectedBackgroundImageUri = Uri.parse(uriString)
                        backgroundImageView.setImageURI(selectedBackgroundImageUri)
                    }
                }
            }
        }

        // Initially hide the email input field and submit button
        emailInputLayout.visibility = View.GONE
        btnSubmitEmail.visibility = View.GONE

        // Show email input when "Add User" button is clicked
        btnAddUser.setOnClickListener {
            emailInputLayout.visibility = View.VISIBLE  // Show email input field
            btnSubmitEmail.visibility = View.VISIBLE  // Show submit button
        }

        // Handle Add User functionality
        btnSubmitEmail.setOnClickListener {
            val userEmail = edtEmail.text.toString().trim()

            if (userEmail.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = userDao.getUserByEmail(userEmail)

                    if (user != null) {
                        Log.d("ProfileAdminActivity", "User found: ${user.email}, Role: ${user.role}")

                        if (user.role.toLowerCase() == "user") {  // Ensure the comparison is case-insensitive
                            // Show user profile picture only (do not show name/email/id)
                            val profileImageUser = findViewById<ImageButton>(R.id.profileImageUser)
                            user.profileImageUrl?.let { uriString ->
                                selectedProfileImageUri = Uri.parse(uriString)
                                profileImageUser.setImageURI(selectedProfileImageUri)  // Set user's profile image here
                            }

                            // Update group admin ID for this user
                            val updatedUser = user.copy(groupAdminId = user.userId)  // Update groupAdminId
                            userDao.update(updatedUser)

                            // Provide feedback
                            Toast.makeText(this@ProfileAdminActivity, "User added successfully", Toast.LENGTH_SHORT).show()

                            // Hide the email input and submit button after submitting
                            emailInputLayout.visibility = View.GONE
                            btnSubmitEmail.visibility = View.GONE

                            // Reload the profile image after adding the user
                            reloadUserProfileImage()
                        } else {
                            // If user is not a "user"
                            Toast.makeText(this@ProfileAdminActivity, "User role is not 'user'", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // If user is not found
                        Log.d("ProfileAdminActivity", "User not found")
                        Toast.makeText(this@ProfileAdminActivity, "User not found or invalid role", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Handle Profile Image Click (Admin profile image)
        profileImageView.setOnClickListener {
            requestPermissions()
            if (checkPermission()) {
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 100) // Request code for profile image
            }
        }

        // Handle Background Image Click
        backgroundImageView.setOnClickListener {
            requestPermissions()
            if (checkPermission()) {
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 200) // Request code for background image
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
            Toast.makeText(this, "Language settings", Toast.LENGTH_SHORT).show()
        }

        btnTheme.setOnClickListener {
            Toast.makeText(this, "Theme settings", Toast.LENGTH_SHORT).show()
        }

        btnChangePassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun reloadUserProfileImage() {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                it.profileImageUrl?.let { uriString ->
                    selectedProfileImageUri = Uri.parse(uriString)
                    profileImageView.setImageURI(selectedProfileImageUri)
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

    // Save profile image URL to database
    private fun saveProfileImageUri(uri: Uri) {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                val updatedUser = it.copy(profileImageUrl = uri.toString())
                userDao.update(updatedUser)
                Toast.makeText(this@ProfileAdminActivity, "Profile image updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBackgroundImageUri(uri: Uri) {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                val updatedUser = it.copy(backgroundImageUrl = uri.toString())
                userDao.update(updatedUser)
                Toast.makeText(this@ProfileAdminActivity, "Background image updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Request storage permissions
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    // Check permission status
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

    // Navigation Functions
    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageAdminActivity::class.java)
        startActivity(intent)
    }

    fun goToTaskView(view: android.view.View) {
        val intent = Intent(this, TaskAdminActivity::class.java)
        startActivity(intent)
    }

    fun goToAnalytics(view: android.view.View) {
        val intent = Intent(this, AnalyticsActivity::class.java)
        startActivity(intent)
    }

    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileAdminActivity::class.java)
        startActivity(intent)
    }
}