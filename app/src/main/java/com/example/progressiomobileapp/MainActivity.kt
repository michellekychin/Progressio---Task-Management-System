package com.example.progressiomobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.progressiomobileapp.ui.theme.ProgressioMobileAppTheme
import androidx.room.Room
import kotlinx.coroutines.runBlocking // For simple demo; use coroutines properly!
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.User

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProgressioMobileAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")

                    // Room database instance
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java, "app_database" // Replace with your database name!
                    ).build()

                    val userDao = db.userDao()

                    // Example usage (in a real app, do this in a coroutine!)
                    runBlocking {
                        val newUser = User(name = "John Doe", email = "john@example.com", password = "password123", role = "user", groupAdminId = null)
                        userDao.insert(newUser)
                        val retrievedUser = userDao.getUserByEmail("john@example.com")
                        println("Retrieved user: $retrievedUser")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProgressioMobileAppTheme {
        Greeting("Android")
    }
}