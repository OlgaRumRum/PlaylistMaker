package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.ThemeSettings

interface SettingsThemeStorage {
    fun saveThemeSettings(settings: ThemeSettings)
    fun getThemeSettings(): ThemeSettings
}