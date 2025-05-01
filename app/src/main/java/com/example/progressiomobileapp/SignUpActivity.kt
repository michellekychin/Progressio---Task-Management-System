package com.example.progressiomobileapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.User
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase


class SignUpActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var btnUser: Button
    private lateinit var btnAdmin: Button
    private lateinit var btnSignUp: Button
    private lateinit var showPasswordBtn: Button  // Show/Hide password button

    private var selectedRole = "User" // Default role

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Views
        nameInput = findViewById(R.id.etName)
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        confirmPasswordInput = findViewById(R.id.etConfirmPassword)
        btnUser = findViewById(R.id.btnUser)
        btnAdmin = findViewById(R.id.btnAdmin)
        btnSignUp = findViewById(R.id.btnSignUp)
        showPasswordBtn = findViewById(R.id.btnShowPassword)  // Initialize the show password button

        // Initialize Room Database and UserDao
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Role selection
        btnUser.setOnClickListener {
            selectedRole = "User"
            btnUser.setBackgroundColor(Color.GRAY)
            btnAdmin.setBackgroundColor(Color.LTGRAY)
            Toast.makeText(this, "Role set to User", Toast.LENGTH_SHORT).show()
        }

        btnAdmin.setOnClickListener {
            selectedRole = "Admin"
            btnAdmin.setBackgroundColor(Color.GRAY)
            btnUser.setBackgroundColor(Color.LTGRAY)
            Toast.makeText(this, "Role set to Admin", Toast.LENGTH_SHORT).show()
        }

        // Show password toggle functionality
        showPasswordBtn.setOnClickListener {
            // Toggle visibility of both password and confirm password fields
            if (passwordInput.inputType == 129) {  // 129 means textPassword
                passwordInput.inputType = 1  // 1 means textVisible
                confirmPasswordInput.inputType = 1  // Make confirm password visible
                passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)  // Change icon to eye open
                confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)  // Change icon to eye open for confirm password
                showPasswordBtn.text = "Hide Password"
            } else {
                passwordInput.inputType = 129  // textPassword
                confirmPasswordInput.inputType = 129  // textPassword for confirm password
                passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)  // Change icon to eye closed
                confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)  // Change icon to eye closed for confirm password
                showPasswordBtn.text = "Show Password"
            }
        }

        // SignUp button click listener
        btnSignUp.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            // Input validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if email is already in use
            lifecycleScope.launch {
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "Email is already in use. Please try a different email.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Email validation
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "Invalid email format", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Password validation
                if (!isValidPassword(password)) {
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "Password must be at least 8 characters, contain a number, an uppercase letter, and a special character", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Check if passwords match
                if (password != confirmPassword) {
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Save user data in the Room database
                val user = User(name = name, email = email, password = password, role = selectedRole, groupAdminId = null)

                // Insert user into the database
                val userId = userDao.insert(user)

                // Role-based navigation
                if (selectedRole == "Admin") {
                    // If the role is Admin, navigate to HomepageAdminActivity
                    val intent = Intent(this@SignUpActivity, HomepageAdminActivity::class.java)
                    startActivity(intent)
                    finish()  // Close SignUpActivity so it doesn't remain in the back stack
                } else {
                    // If the role is User, navigate to HomepageUserActivity
                    val intent = Intent(this@SignUpActivity, HomepageUserActivity::class.java)
                    startActivity(intent)
                    finish()  // Close SignUpActivity so it doesn't remain in the back stack
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
