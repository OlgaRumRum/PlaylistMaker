package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarArrowBack.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.themeSwitcher.isChecked = (applicationContext as App).darkTheme


        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).darkTheme = checked
        }

        binding.shareTheApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_share))
            intent.type = "text/plain"
            startActivity(intent)
        }

        binding.writeToSupport.setOnClickListener {

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

        binding.userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.user_agreement_button))
            startActivity(intent)
        }

    }

}