package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addNewPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun deletePlaylist(playlist: Playlist)
}
