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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class ProfileUserActivity : BaseActivity() {

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
        setupBottomNavigationUser(R.id.nav_profile)

        // Initialize Views
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvId = findViewById(R.id.tvId)
        profileImageView = findViewById(R.id.profileImage)
        backgroundImageView = findViewById(R.id.backgroundImage)
        btnLogout = findViewById(R.id.btnLogout)
        btnLanguage = findViewById(R.id.btnLanguage)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        profileImageAdmin = findViewById(R.id.profileImageAdmin) // Initialize admin profile image view

        // Initialize Room Database and DAOs
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        adminDao = db.adminDao() // Get the admin DAO

        currentUserEmail = sharedPreferences.getString("email", "") ?: ""

        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                // Set user details
                tvName.text = it.name
                tvEmail.text = it.email
                tvId.text = "ID: ${it.userId}"

                currentUserGroupAdminId = it.groupAdminId?.toString() ?: ""

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
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, 100) // Request code for profile image
            } else {
                requestPermissions()
            }
        }

        // Handle Background Image Click
        backgroundImageView.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, 200) // Request code for background image
            } else {
                requestPermissions()
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
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, which ->
            // Redirect to SignInActivity on clicking 'Yes'
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()  // Optionally finish this activity so the user cannot return to it by pressing back
        }
        builder.setNegativeButton("No") { dialog, which ->
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

    // Function to request permissions
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    // Function to check if permissions are granted
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Permission denied
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                    .show()
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
                Toast.makeText(
                    this@ProfileUserActivity,
                    getString(R.string.profile_image_updated),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveBackgroundImageUri(uri: Uri) {
        lifecycleScope.launch {
            val user = userDao.getUserByEmail(currentUserEmail)
            user?.let {
                val updatedUser = it.copy(backgroundImageUrl = uri.toString())
                userDao.update(updatedUser)
                Toast.makeText(
                    this@ProfileUserActivity,
                    getString(R.string.background_image_updated),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
