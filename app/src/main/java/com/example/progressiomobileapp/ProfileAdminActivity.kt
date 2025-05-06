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
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class ProfileAdminActivity : BaseActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvId: TextView
    private lateinit var profileImageView: ImageButton
    private lateinit var backgroundImageView: ImageButton
    private lateinit var btnLogout: TextView
    private lateinit var btnLanguage: TextView
    private lateinit var btnTheme: TextView
    private lateinit var btnChangePassword: TextView
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
        setupBottomNavigation(R.id.nav_profile)

        // Initialize Views
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvId = findViewById(R.id.tvId)
        profileImageView = findViewById(R.id.profileImage)
        backgroundImageView = findViewById(R.id.backgroundImage)
        btnLogout = findViewById(R.id.btnLogout)
        btnLanguage = findViewById(R.id.btnLanguage)
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
                        Log.d(
                            "ProfileAdminActivity",
                            "User found: ${user.email}, Role: ${user.role}"
                        )

                        if (user.role.toLowerCase() == "user") {  // Ensure the comparison is case-insensitive
                            // Show user profile picture only (do not show name/email/id)
                            val profileImageUser = findViewById<ImageButton>(R.id.profileImageUser)
                            user.profileImageUrl?.let { uriString ->
                                selectedProfileImageUri = Uri.parse(uriString)
                                profileImageUser.setImageURI(selectedProfileImageUri)  // Set user's profile image here
                            }

                            // Update group admin ID for this user
                            val updatedUser =
                                user.copy(groupAdminId = user.userId)  // Update groupAdminId
                            userDao.update(updatedUser)

                            // Provide feedback
                            Toast.makeText(
                                this@ProfileAdminActivity,
                                getString(R.string.user_added_successfully),
                                Toast.LENGTH_SHORT
                            ).show()

                            // Hide the email input and submit button after submitting
                            emailInputLayout.visibility = View.GONE
                            btnSubmitEmail.visibility = View.GONE

                            // Reload the profile image after adding the user
                            reloadUserProfileImage()
                        } else {
                            // If user is not a "user"
                            Toast.makeText(
                                this@ProfileAdminActivity,
                                getString(R.string.user_role_is_not_user),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // If user is not found
                        Log.d("ProfileAdminActivity", "User not found")
                        Toast.makeText(
                            this@ProfileAdminActivity,
                            getString(R.string.user_not_found_or_invalid_role),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Handle Profile Image Click (Admin profile image)
        profileImageView.setOnClickListener {
            requestPermissions()
            if (checkPermission()) {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, 100) // Request code for profile image
            }
        }

        // Handle Background Image Click
        backgroundImageView.setOnClickListener {
            requestPermissions()
            if (checkPermission()) {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, 200) // Request code for background image
            }
        }

        // Check for language preference from SharedPreferences
        val settingsSharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val language = settingsSharedPreferences.getString("language", "en") // Default to "en" if no language is set

        if (language != null && language != "en") {
            setLocale(language)
        }

        // Set up listeners for the settings
        val btnLanguage = findViewById<TextView>(R.id.btnLanguage)
        val btnChangePassword = findViewById<TextView>(R.id.btnChangePassword)
        val btnLogout = findViewById<TextView>(R.id.btnLogout)

        // Set click listeners for each button
        btnLanguage.setOnClickListener {
            onLanguageClicked()
        }

        btnChangePassword.setOnClickListener {
            onChangePasswordClicked()
        }

        btnLogout.setOnClickListener {
            onLogoutClicked()
        }
    }

    // Function to handle Language setting click
    private fun onLanguageClicked() {
        val intent = Intent(this, LanguageSelectionActivity::class.java)
        startActivity(intent)
    }

    // Function to handle Change Password click
    private fun onChangePasswordClicked() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    // Function to handle Log Out click
    private fun onLogoutClicked() {
        showLogoutConfirmationDialog()
    }

    // Logout confirmation dialog
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.log_out))
        builder.setMessage(getString(R.string.logout_confirmation_message))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            // Redirect to SignInActivity on clicking 'Yes'
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()  // Optionally finish this activity so the user cannot return to it by pressing back
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            // Just dismiss the dialog if 'No' is clicked
            dialog.dismiss()
        }
        builder.create().show()
    }

    // Set the locale of the app based on the selected language
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
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
                Toast.makeText(this@ProfileAdminActivity, getString(R.string.profile_image_updated), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBackgroundImageUri(uri: Uri) {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                val updatedUser = it.copy(backgroundImageUrl = uri.toString())
                userDao.update(updatedUser)
                Toast.makeText(this@ProfileAdminActivity, getString(R.string.background_image_updated), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

}