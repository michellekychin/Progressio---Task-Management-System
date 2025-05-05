package com.example.progressiomobileapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LanguageSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        // Get the saved language preference
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("language", "en") // Default to "en" if not set

        // Find the language buttons and tick marks
        val btnBahasa = findViewById<TextView>(R.id.btnBahasa)
        val btnChinese = findViewById<TextView>(R.id.btnChinese)
        val btnEnglish = findViewById<TextView>(R.id.btnEnglish)

        val tickBahasa = findViewById<ImageView>(R.id.tickBahasa)
        val tickChinese = findViewById<ImageView>(R.id.tickChinese)
        val tickEnglish = findViewById<ImageView>(R.id.tickEnglish)

        // Set the correct tick visibility based on saved language
        when (savedLanguage) {
            "ms" -> tickBahasa.visibility = ImageView.VISIBLE
            "zh" -> tickChinese.visibility = ImageView.VISIBLE
            "en" -> tickEnglish.visibility = ImageView.VISIBLE
        }

        // Set click listeners for each language option
        btnBahasa.setOnClickListener {
            setLocale("ms") // Bahasa Malaysia
            tickBahasa.visibility = ImageView.VISIBLE
            tickChinese.visibility = ImageView.GONE
            tickEnglish.visibility = ImageView.GONE
            restartApp()
        }

        btnChinese.setOnClickListener {
            setLocale("zh") // Simplified Chinese
            tickBahasa.visibility = ImageView.GONE
            tickChinese.visibility = ImageView.VISIBLE
            tickEnglish.visibility = ImageView.GONE
            restartApp()
        }

        btnEnglish.setOnClickListener {
            setLocale("en") // English
            tickBahasa.visibility = ImageView.GONE
            tickChinese.visibility = ImageView.GONE
            tickEnglish.visibility = ImageView.VISIBLE
            restartApp()
        }
    }

    // Method to update the language and store it in SharedPreferences
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the language preference to SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()
    }

    // Restart the app after language change
    private fun restartApp() {
        val intent = Intent(this, ProfileAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}
