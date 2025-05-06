package com.example.progressiomobileapp

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.User
import com.example.progressiomobileapp.data.dao.UserDao
import kotlinx.coroutines.launch
import com.example.progressiomobileapp.data.AppDatabase
import java.util.*
import javax.mail.*
import javax.mail.internet.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var btnSignUp: Button
    private lateinit var showPasswordBtn: Button

    private lateinit var userDao: UserDao
    private var selectedRole = "User" // Default role

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Views
        nameInput = findViewById(R.id.etName)
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        confirmPasswordInput = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        showPasswordBtn = findViewById(R.id.btnShowPassword)

        // Initialize Room Database and UserDao
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Role selection
        val btnUser: Button = findViewById(R.id.btnUser)
        val btnAdmin: Button = findViewById(R.id.btnAdmin)

        btnUser.setOnClickListener {
            selectedRole = "User"
            btnUser.setBackgroundColor(Color.GRAY)
            btnAdmin.setBackgroundColor(Color.LTGRAY)
            Toast.makeText(this, getString(R.string.role_set_to_user), Toast.LENGTH_SHORT).show()
        }

        btnAdmin.setOnClickListener {
            selectedRole = "Admin"
            btnAdmin.setBackgroundColor(Color.GRAY)
            btnUser.setBackgroundColor(Color.LTGRAY)
            Toast.makeText(this, getString(R.string.role_set_to_admin), Toast.LENGTH_SHORT).show()
        }

        // Show password toggle functionality
        showPasswordBtn.setOnClickListener {
            // Toggle visibility of both password and confirm password fields
            if (passwordInput.inputType == 129) {  // 129 means textPassword
                passwordInput.inputType = 1  // 1 means textVisible
                confirmPasswordInput.inputType = 1  // Make confirm password visible
                passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)  // Change icon to eye open
                confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)  // Change icon to eye open for confirm password
                showPasswordBtn.text = getString(R.string.hide_password)
            } else {
                passwordInput.inputType = 129  // textPassword
                confirmPasswordInput.inputType = 129  // textPassword for confirm password
                passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)  // Change icon to eye closed
                confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)  // Change icon to eye closed for confirm password
                showPasswordBtn.text = getString(R.string.show_password)
            }
        }

        // SignUp button click listener
        btnSignUp.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            // Validate input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this,
                    getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate password format
            if (!isValidPassword(password)) {
                Toast.makeText(this, getString(R.string.password_validation), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (password != confirmPassword) {
                Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if email already exists
            lifecycleScope.launch {
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity,
                            getString(R.string.email_exists_message), Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Save user data in the Room database
                val user = User(name = name, email = email, password = password, role = selectedRole.toLowerCase(), groupAdminId = null)

                // Insert user into the database
                userDao.insert(user)

                // Generate a verification code
                val verificationCode = generateVerificationCode()

                // Send email verification
                SendVerificationEmail(email, verificationCode).execute()

                // Inform the user to check their email
                runOnUiThread {
                    Toast.makeText(this@SignUpActivity,
                        getString(R.string.check_email_verification_message), Toast.LENGTH_SHORT).show()
                }

                // Pass the correct verification code to EmailVerificationActivity
                val intent = Intent(this@SignUpActivity, EmailVerificationActivity::class.java)
                intent.putExtra("email", email)  // Pass the email to the verification activity
                intent.putExtra("role", selectedRole)  // Pass the role to the verification activity
                intent.putExtra("verificationCode", verificationCode)  // Pass the correct verification code
                startActivity(intent)
                finish()  // Close SignUpActivity


                // After successfully signing up, store the userName in SharedPreferences
                val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("userEmail", email)
                editor.putString("userName", name)  // Saving the userName
                editor.putString("userRole", selectedRole)
                editor.apply()  // Commit the changes


            }
        }
    }

    // Function to validate the password according to the given rules
    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.*[a-z]).{8,}$".toRegex()
        return password.matches(passwordRegex)
    }

    // Function to generate a random verification code
    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString() // Simple 6-digit code
    }

    // AsyncTask to send email in background
    private class SendVerificationEmail(val email: String, val verificationCode: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val props = Properties()
            props["mail.smtp.host"] = "smtp.gmail.com"  // Gmail SMTP server
            props["mail.smtp.port"] = "587"  // TLS port
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"  // Enable TLS

            // Gmail authentication
            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("tanshiehling@gmail.com", "jxuc dcda zwco dfgj") // Replace with your email and App Password
                }
            })

            return try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress("tanshiehling@gmail.com"))  // Your Gmail address
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))  // Recipient's email address
                message.subject = "Email Verification"  // Subject of the email
                message.setText("Your verification code is: $verificationCode")  // Body of the email

                // Send the email
                Transport.send(message)
                true
            } catch (e: Exception) {
                e.printStackTrace()  // Print stack trace for debugging
                println("Error while sending email: ${e.message}")
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result) {
                // Email sent successfully
                println("Verification email sent to $email")
            } else {
                // Email sending failed
                println("Failed to send verification email to $email")
            }
        }
    }

}
