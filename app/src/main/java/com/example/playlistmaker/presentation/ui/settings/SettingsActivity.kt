package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbarArrowBack = findViewById<Toolbar>(R.id.toolbar_arrow_back)

        toolbarArrowBack.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = (applicationContext as App).darkTheme


        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).darkTheme = checked
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