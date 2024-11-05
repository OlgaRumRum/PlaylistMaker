package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun addNewPlaylist(playlist: Playlist) {
        playlistRepository.addNewPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }
}

