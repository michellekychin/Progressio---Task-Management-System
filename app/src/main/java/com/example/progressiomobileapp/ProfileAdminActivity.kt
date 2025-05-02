package com.example.progressiomobileapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase


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
    private lateinit var userDao: UserDao

    private var selectedProfileImageUri: Uri? = null
    private var selectedBackgroundImageUri: Uri? = null
    private lateinit var currentUserEmail: String

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

        // Initialize Room Database and UserDao
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Replace with the actual logged-in user's email retrieval logic
        currentUserEmail = "ling@example.com"

        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                tvName.text = it.name
                tvEmail.text = it.email
                tvId.text = "ID: ${it.userId}"

                it.profileImageUrl?.let { uriString ->
                    selectedProfileImageUri = Uri.parse(uriString)
                    profileImageView.setImageURI(selectedProfileImageUri)
                }
                it.backgroundImageUrl?.let { uriString ->
                    selectedBackgroundImageUri = Uri.parse(uriString)
                    backgroundImageView.setImageURI(selectedBackgroundImageUri)
                }

                val groupAdminId = it.groupAdminId ?: return@launch
                val groupMembers = userDao.getUsersByGroupAdminId(groupAdminId)
                // You can implement logic here to display the profile pictures of group members
                // For instance, dynamically add ImageViews to the horizontal LinearLayout
                // below the "Group Member" TextView.
            }
        }

        // Handle Profile Image Click
        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100) // Request code for profile image
        }

        // Handle Background Image Click
        backgroundImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 200) // Request code for background image
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

    // Navigate to Home Page
    fun goToHome(view: android.view.View) {
        val intent = Intent(this, HomepageAdminActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Task View Page
    fun goToTaskView(view: android.view.View) {
        val intent = Intent(this, TaskAdminActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Analytics Page
    fun goToAnalytics(view: android.view.View) {
        val intent = Intent(this, AnalyticsActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Calendar Page
    fun goToCalendar(view: android.view.View) {
        val intent = Intent(this, CalenderAdminActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Profile Page
    fun goToProfile(view: android.view.View) {
        val intent = Intent(this, ProfileAdminActivity::class.java)
        startActivity(intent)
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
}