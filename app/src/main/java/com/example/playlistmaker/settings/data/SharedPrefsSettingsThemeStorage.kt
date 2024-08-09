package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.settings.domain.ThemeSettings


class SharedPrefsSettingsThemeStorage(private val sharedPreferences: SharedPreferences) :
    SettingsThemeStorage {

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(THEME_KEY, false)
        return ThemeSettings(isDark)
    }


    override fun saveThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit {
            putBoolean(THEME_KEY, settings.darkTheme)
        }
    }

    companion object {
        const val THEME_KEY = "dark_theme"
    }
}