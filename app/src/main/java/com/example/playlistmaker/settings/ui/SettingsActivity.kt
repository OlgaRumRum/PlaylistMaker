package com.example.playlistmaker.settings.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val settingsInteractor = Creator.provideSettingsInteractor()
    private val sharingInteractor = Creator.provideSharingInteractor()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(
                settingsInteractor,
                sharingInteractor
            )
        )[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarArrowBack.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.themeSettings.observe(this) { settings ->
            binding.themeSwitcher.isChecked = settings.darkTheme
        }


        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateThemeSettings(isChecked)
            (application as App).saveThemeToSharedPreferences(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }


        binding.shareTheApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.openSupport()

        }

        binding.userAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }
}



