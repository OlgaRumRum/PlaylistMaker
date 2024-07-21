package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate


const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val THEME_KEY = "key_for_the_theme"

/*class App : Application() {

var darkTheme = false
    private set

override fun onCreate() {
    super.onCreate()
    val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
    darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
    switchTheme(darkTheme)

    Creator.initApplication(this)
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

 */

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            MODE_PRIVATE
        ) // Инициализация sharedPreferences
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)

        // Инициализация других компонентов, если необходимо
        Creator.initApplication(this)
    }

    var darkTheme: Boolean
        get() = sharedPrefs.getBoolean(THEME_KEY, false)
        set(value) {
            sharedPrefs.edit()
                .putBoolean(THEME_KEY, value)
                .apply()
            switchTheme(value)
        }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
