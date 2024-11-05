package com.example.playlistmaker.media.ui.newPlaylist

sealed interface NewPlaylistState {
    object Empty : NewPlaylistState

    object NotEmpty : NewPlaylistState
}