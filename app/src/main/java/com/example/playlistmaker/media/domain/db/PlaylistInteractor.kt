package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addNewPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistsFlow(): Flow<List<Playlist>>

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(playlistId: Playlist, track: Track)

    suspend fun getTracks(ids: List<Long>): List<Track>

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)

    suspend fun updatePlaylist(playlist: Playlist)

}
