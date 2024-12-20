package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun addNewPlaylist(playlist: Playlist) {
        playlistRepository.addNewPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override fun getPlaylistsFlow(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylistsFlow()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlistId: Playlist, track: Track) {
        playlistRepository.addTrackToPlaylist(playlistId, track)
    }

    override suspend fun getTracks(ids: List<Long>): List<Track> {
        return playlistRepository.getTracks(ids)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? =
        playlistRepository.getPlaylistById(playlistId)

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        return playlistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

}

