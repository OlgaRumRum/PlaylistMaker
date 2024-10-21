package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.TracksDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.TracksEntity
import com.example.playlistmaker.media.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val tracksDbConvertor: TracksDbConvertor,
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = tracksDbConvertor.map(track)
        appDatabase.favoriteTracksDao().insertTracks(listOf(trackEntity))
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = tracksDbConvertor.map(track)
        appDatabase.favoriteTracksDao().deleteTracks(trackEntity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoriteTracksDao().getFavoriteList()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TracksEntity>): List<Track> {
        return tracks.map { track -> tracksDbConvertor.map(track) }
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return appDatabase.favoriteTracksDao().isFavorite(trackId) > 0
    }


}

