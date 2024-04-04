package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowBackButton = findViewById<Button>(R.id.arrow_back)

        arrowBackButton.setOnClickListener {

            val displayIntent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(displayIntent)

        }
    }
}