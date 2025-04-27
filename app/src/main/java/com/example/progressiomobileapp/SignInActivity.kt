package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var btnSignIn: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize views
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Initialize Room Database and UserDao
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Set click listener for Sign In button
        btnSignIn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Input validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check user credentials in the Room database
            lifecycleScope.launch {
                val user = userDao.getUserByEmail(email)

                if (user != null && user.password == password) {
                    // Check if the user is an admin or regular user
                    if (user.role == "Admin") {
                        // Admin user, navigate to HomepageAdminActivity
                        val intent = Intent(this@SignInActivity, HomepageAdminActivity::class.java)
                        startActivity(intent)
                        finish()  // Close SignInActivity so it doesn't remain in the back stack
                    } else {
                        // Regular user, navigate to HomepageUserActivity
                        val intent = Intent(this@SignInActivity, HomepageUserActivity::class.java)
                        startActivity(intent)
                        finish()  // Close SignInActivity so it doesn't remain in the back stack
                    }
                } else {
                    // Invalid credentials
                    runOnUiThread {
                        Toast.makeText(this@SignInActivity, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



        // Link to Sign Up Activity
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Link to Forgot Password Activity
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}
