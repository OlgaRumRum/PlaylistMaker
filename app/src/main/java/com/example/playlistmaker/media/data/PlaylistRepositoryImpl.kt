package com.example.playlistmaker.media.data

import android.util.Log
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun addNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        try {
            appDatabase.playlistDao().insertPlaylist(listOf(playlistEntity))
            Log.d("PlaylistRepository", "Playlist added successfully: $playlistEntity")
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error adding playlist: ${e.message}", e)
            throw e
        }
    }


    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromEntity(playlists))
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
}