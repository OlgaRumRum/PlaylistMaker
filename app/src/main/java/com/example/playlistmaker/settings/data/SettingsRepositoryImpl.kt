package com.example.playlistmaker.settings.data


import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(private val storage: SettingsThemeStorage) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val isDark = storage.getThemeSettings().darkTheme
        return ThemeSettings(isDark)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.saveThemeSettings(settings)
    }
}