package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbarArrowBack = findViewById<Toolbar>(R.id.toolbar_arrow_back)

        toolbarArrowBack.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val switchThemeDark = findViewById<SwitchCompat>(R.id.switch_compat)
        switchThemeDark.setOnClickListener {
            Toast.makeText(this@SettingsActivity, "Темная тема", Toast.LENGTH_SHORT).show()
        }

        val shareButton = findViewById<TextView>(R.id.share_the_app)
        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_share))
            intent.type = "text/plain"
            startActivity(intent)
        }

        val supportButton = findViewById<TextView>(R.id.write_to_support)
        supportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.email_address))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_line))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.context_of_the_message))
            startActivity(intent)
        }

        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)
        userAgreementButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.user_agreement_button))
            startActivity(intent)
        }

    }

}