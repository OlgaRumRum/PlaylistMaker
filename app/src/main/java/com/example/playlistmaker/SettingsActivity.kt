package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbarArrowBack = findViewById<Toolbar>(R.id.toolbar_arrow_back)

        toolbarArrowBack.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val switchThemeDark = findViewById<SwitchCompat>(R.id.switch_compat)

        switchThemeDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        switchThemeDark.isChecked =
            resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES


        val shareButton = findViewById<TextView>(R.id.share_the_app)
        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_share))
            intent.type = "text/plain"
            startActivity(intent)
        }

        val supportButton = findViewById<TextView>(R.id.write_to_support)
        supportButton.setOnClickListener {

            val email = resources.getString(R.string.email_address)
            val subject = resources.getString(R.string.subject_line)
            val context = resources.getString(R.string.context_of_the_message)

            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, context)
            }, getString(R.string.practikum))
            startActivity(share)
        }

        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)
        userAgreementButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.user_agreement_button))
            startActivity(intent)
        }

    }


}