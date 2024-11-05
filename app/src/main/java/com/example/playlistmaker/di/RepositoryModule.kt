package com.example.playlistmaker.di

import com.example.playlistmaker.audioPlayer.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerRepository
import com.example.playlistmaker.media.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.TracksDbConvertor
import com.example.playlistmaker.media.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory { TracksDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConverter() }


}