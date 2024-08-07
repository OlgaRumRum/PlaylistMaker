package com.example.playlistmaker.settings.domain


class SettingsInteractorImpl(val repository: SettingsRepository) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }
}
