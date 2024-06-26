package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val searchButton = findViewById<Button>(R.id.search_button)


        val searchButtonListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(displayIntent)

            }
        }
        searchButton.setOnClickListener(searchButtonListener)


        val libraryButton = findViewById<Button>(R.id.library_button)

        libraryButton.setOnClickListener {

            val displayIntent = Intent(this@MainActivity, LibraryActivity::class.java)
            startActivity(displayIntent)

        }

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {


            val displayIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(displayIntent)

        }

    }
}