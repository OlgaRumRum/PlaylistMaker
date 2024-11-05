package com.example.playlistmaker.media.ui.playlist

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistState {
    //object Loading : PlaylistState
    data class Content(val playlists: List<Playlist>) : PlaylistState

    //object Empty : PlaylistState
    data class Empty(
        val message: String
    ) : PlaylistState
}