package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator



class App : Application() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        sharedPrefs = Creator.provideSharedPreferences()
        loadThemeFromSharedPreferences()
        context = this
    }

    private fun loadThemeFromSharedPreferences() {
        val isDark = sharedPrefs.getBoolean(THEME_KEY, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun saveThemeToSharedPreferences(isDark: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_KEY, isDark).apply()
    }

    companion object {
        const val THEME_KEY = "dark_theme"


        private lateinit var context: Application

        fun getStringFromResources(resourceId: Int): String {
            return context.getString(resourceId)
        }

    }
}


