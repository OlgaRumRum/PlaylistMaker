package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
) : PlaylistRepository {

    override suspend fun addNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        try {
            appDatabase.playlistDao().insertPlaylist(playlistEntity)
        } catch (e: Exception) {
            throw e
        }
    }


    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromEntity(playlists))
    }

    override fun getPlaylistsFlow(): Flow<List<Playlist>> = flow {
        appDatabase.playlistDao().getAllPlaylistsFlow().collect { playlists ->
            emit(convertFromEntity(playlists))
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().deletePlaylistById(playlistEntity.id)
    }

    private fun convertFromEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { track ->
            playlistDbConverter.map(track)
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {

        try {
            val playlistTrackEntity = playlistTrackDbConverter.map(track)
            appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
            appDatabase.playlistDao().insertPlaylistTrack(playlistTrackEntity)
        } catch (e: Exception) {
            throw e
        }
    }
}