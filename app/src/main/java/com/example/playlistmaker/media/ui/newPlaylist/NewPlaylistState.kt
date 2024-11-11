package com.example.playlistmaker.media.ui.newPlaylist

sealed interface NewPlaylistState {
    data object Empty : NewPlaylistState

    data object NotEmpty : NewPlaylistState
}