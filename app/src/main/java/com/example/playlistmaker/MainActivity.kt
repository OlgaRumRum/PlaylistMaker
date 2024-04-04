package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val searchButton = findViewById<Button>(R.id.search_button)


        val searchButtonListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Кнопка Поиск", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(searchButtonListener)


        val libraryButton = findViewById<Button>(R.id.library_button)

        libraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Кнопка Медиатека", Toast.LENGTH_SHORT).show()
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Кнопка Настройки", Toast.LENGTH_SHORT).show()
        }

    }
}