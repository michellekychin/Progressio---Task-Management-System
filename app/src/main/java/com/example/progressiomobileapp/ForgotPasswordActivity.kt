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


class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var btnSubmitNewPassword: Button
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize views
        emailInput = findViewById(R.id.etForgotEmail)
        nameInput = findViewById(R.id.etForgotName)
        newPasswordInput = findViewById(R.id.etNewPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        btnSubmitNewPassword = findViewById(R.id.btnSubmitNewPassword)

        // Initialize Room Database and UserDao
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Set click listener for "Get Password" button
        btnResetPassword.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val name = nameInput.text.toString().trim()

            // Input validation
            if (email.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please enter both your name and email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the email and name match a user in the Users table
            lifecycleScope.launch {
                val user = userDao.getUserByEmail(email)

                if (user != null && user.name == name) {
                    // Show the new password field and submit button
                    runOnUiThread {
                        newPasswordInput.visibility = EditText.VISIBLE
                        btnSubmitNewPassword.visibility = Button.VISIBLE
                        btnResetPassword.visibility = Button.GONE
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ForgotPasswordActivity, "Name and email do not match", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Set click listener for "Submit New Password" button
        btnSubmitNewPassword.setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()

            // Input validation
            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate the new password
            if (!isValidPassword(newPassword)) {
                Toast.makeText(this, "Password must be at least 8 characters, contain a number, an uppercase letter, and a special character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the email and name to find the correct user
            val email = emailInput.text.toString().trim()
            val name = nameInput.text.toString().trim()

            lifecycleScope.launch {
                val user = userDao.getUserByEmail(email)

                if (user != null && user.name == name) {
                    // Update the password in the database
                    user.password = newPassword
                    userDao.update(user)

                    // Show success message
                    runOnUiThread {
                        Toast.makeText(this@ForgotPasswordActivity, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        // Optionally, navigate to the sign-in page or home page
                        // Navigate to SignInActivity
                        val intent = Intent(this@ForgotPasswordActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()  // Close ForgotPasswordActivity so it doesn't remain in the back stack
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ForgotPasswordActivity, "Name and email do not match", Toast.LENGTH_SHORT).show()
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
