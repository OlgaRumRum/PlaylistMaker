package com.example.playlistmaker.media.data

import android.util.Log
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity
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
        for (trackId in playlist.trackIds) {
            isTrackInOtherPlaylists(trackId)
        }
    }

    private fun convertFromEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { track ->
            playlistDbConverter.map(track)
        }
    }

    private fun convertFromPlaylistTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {
        return tracks.map { track ->
            playlistTrackDbConverter.map(track)
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Playlist, track: Track) {

        try {
            val playlistTrackEntity = playlistTrackDbConverter.map(track)
            appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlistId))
            appDatabase.playlistDao().insertPlaylistTrack(playlistTrackEntity)
        } catch (e: Exception) {
            throw e

        }
    }


    override suspend fun getTracks(ids: List<Long>): List<Track> {
        val allTracks = appDatabase.playlistDao().getTracks()
        val filteredTracks = allTracks.filter { trackEntity -> ids.contains(trackEntity.trackId) }
        return convertFromPlaylistTrackEntity(filteredTracks)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
        return playlistEntity?.let { playlistDbConverter.map(it) }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        val playlist = getPlaylistById(playlistId) ?: return
        val tracks = playlist.trackIds.toMutableList()
        tracks.remove(trackId)
        val updatedPlaylist = playlist.copy(trackIds = tracks.toList(), trackCount = tracks.size)
        Log.d("MY_TAG", tracks.size.toString())
        addNewPlaylist(updatedPlaylist)

        val playlists = appDatabase.playlistDao().getAllPlaylists()
        var isTrackInOtherPlaylist = false
        for (playlist in playlists) {
            if (playlist.trackIds.contains(trackId.toString())) {
                isTrackInOtherPlaylist = true
                break
            }
        }
        if (!isTrackInOtherPlaylist) {
            appDatabase.playlistDao().deleteTrackById(trackId)
        }
    }

    private suspend fun isTrackInOtherPlaylists(trackId: Long) {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        var isTrackInOtherPlaylist = false
        for (playlist in playlists) {
            if (playlist.trackIds.contains(trackId.toString())) {
                isTrackInOtherPlaylist = true
                break
            }
        }

        if (!isTrackInOtherPlaylist) {
            appDatabase.playlistDao().deleteTrackById(trackId)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = playlist.trackIds.toString(),
            trackCount = playlist.trackCount
        )
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

}




