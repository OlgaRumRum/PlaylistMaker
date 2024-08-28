package com.example.playlistmaker.di

import com.example.playlistmaker.audioPlayer.ui.AudioPlayerViewModel
import com.example.playlistmaker.media.ui.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.PlaylistViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { AudioPlayerViewModel(get()) }

    viewModel { SearchViewModel(get(), get()) }

    viewModel { SettingsViewModel(get(), get()) }

    viewModel { FavoriteTracksViewModel() }

    viewModel { PlaylistViewModel() }

}