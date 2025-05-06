package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase
import android.util.Log

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Initialize views
        currentPasswordInput = findViewById(R.id.etCurrentPassword)
        newPasswordInput = findViewById(R.id.etNewPassword)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)

        // Initialize Room Database and UserDao
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Set click listener for "Save Changes" button
        btnSaveChanges.setOnClickListener {
            val currentPassword = currentPasswordInput.text.toString().trim()  // Trim input password
            val newPassword = newPasswordInput.text.toString().trim()  // Trim new password input

            // Input validation
            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, getString(R.string.input_validation), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate the new password
            if (!isValidPassword(newPassword)) {
                Toast.makeText(this, getString(R.string.password_validation), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Fetch the current user's email dynamically from SharedPreferences or session
            val email = getSharedPreferences("UserData", MODE_PRIVATE).getString("userEmail", "") ?: ""
            if (email.isEmpty()) {
                Toast.makeText(this, "User email is not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log the email for debugging purposes
            Log.d("ChangePasswordActivity", "Attempting to change password for email: $email")

            lifecycleScope.launch {
                val user = userDao.getUserByEmail(email)

                if (user != null) {
                    // Log the current password entered and the stored password
                    Log.d("ChangePasswordActivity", "Entered password: $currentPassword")
                    Log.d("ChangePasswordActivity", "Stored password: ${user.password.trim()}")

                    if (user.password.trim() == currentPassword) {
                        // Update the password in the database
                        user.password = newPassword
                        userDao.update(user)

                        // Show success message
                        runOnUiThread {
                            Toast.makeText(this@ChangePasswordActivity,
                                getString(R.string.password_updated_successfully), Toast.LENGTH_SHORT).show()
                            // Optionally, navigate to the sign-in page or home page
                            val intent = Intent(this@ChangePasswordActivity, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // Log the case where password doesn't match
                        Log.d("ChangePasswordActivity", "Password mismatch!")
                        runOnUiThread {
                            Toast.makeText(this@ChangePasswordActivity,
                                getString(R.string.current_password_is_incorrect), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Log the case where no user is found with that email
                    Log.d("ChangePasswordActivity", "No user found with the provided email: $email")
                    runOnUiThread {
                        Toast.makeText(this@ChangePasswordActivity,
                            getString(R.string.user_not_found), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Function to validate the password according to the given rules
    private fun isValidPassword(password: String): Boolean {
        // Check if the password has at least 8 characters, contains a number, an uppercase letter, and a special character
        val passwordRegex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.*[a-z]).{8,}$".toRegex()
        return password.matches(passwordRegex)
    }
}
