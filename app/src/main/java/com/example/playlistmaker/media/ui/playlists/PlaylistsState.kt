package com.example.playlistmaker.media.ui.playlists

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistsState {

    data class Content(val playlists: List<Playlist>) : PlaylistsState
    data class Empty(
        val message: String
    ) : PlaylistsState
}