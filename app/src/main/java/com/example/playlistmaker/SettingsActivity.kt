package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbarArrowBack = findViewById<Toolbar>(R.id.toolbar_arrow_back)

        toolbarArrowBack.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}