package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.ApplicationContextProvider
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.presentation.ui.search.TrackAdapter


object Creator {

    private lateinit var applicationContext: Context
    private lateinit var adapter: TrackAdapter

    fun init(context: Context, adapter: TrackAdapter) { // Добавляем инициализатор, передаем adapter
        this.applicationContext = context
        this.adapter = adapter
    }

    private fun getTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideSearchHistoryRepository(): SearchHistoryRepository { // Добавьте метод для создания SearchHistoryRepository
        return SearchHistoryRepositoryImpl(
            ApplicationContextProvider(applicationContext),
            adapter
        ) // Теперь applicationContext доступен
    }
}