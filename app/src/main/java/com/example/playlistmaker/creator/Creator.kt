package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.audioPlayer.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerRepository
import com.example.playlistmaker.audioPlayer.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsThemeStorage
import com.example.playlistmaker.settings.data.SharedPrefsSettingsThemeStorage
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl


object Creator {

    private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    private lateinit var application: Application


    fun initApplication(application: Application) {
        Creator.application = application
    }

    private fun getTrackRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): SearchInteractor {
        return SearchInteractorImpl(getTrackRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(provideAudioPlayerRepository())
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(provideExternalNavigator())
    }

    private fun provideExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(application)
    }

    fun provadeSettingsStorage(): SettingsThemeStorage {
        return SharedPrefsSettingsThemeStorage(provideSharedPreferences())

    }

    fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(provadeSettingsStorage())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }


    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

}