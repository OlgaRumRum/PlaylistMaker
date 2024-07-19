package com.example.playlistmaker.data

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val THEME_KEY = "key_for_the_theme"

class App : Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}