package com.example.playlistmaker.di

import com.example.playlistmaker.audioPlayer.ui.AudioPlayerViewModel
import com.example.playlistmaker.media.ui.favorite.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.newPlaylist.NewPlaylistViewModel
import com.example.playlistmaker.media.ui.playlist.OnePlaylistViewModel
import com.example.playlistmaker.media.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { AudioPlayerViewModel(get(), get(), get()) }

    viewModel { SearchViewModel(get(), get()) }

    viewModel { SettingsViewModel(get(), get()) }

    viewModel { FavoriteTracksViewModel(androidContext(), get()) }

    viewModel { PlaylistsViewModel(androidContext(), get()) }

    viewModel { NewPlaylistViewModel(get()) }

    viewModel { OnePlaylistViewModel(androidContext(), get()) }



}