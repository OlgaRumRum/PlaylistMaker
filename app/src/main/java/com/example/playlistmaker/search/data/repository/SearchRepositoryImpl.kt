package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.media.data.converters.TracksDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.Resource
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
    private val tracksDbConvertor: TracksDbConvertor,
) : SearchRepository {

    override fun search(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Network error"))
            }
            200 -> {
                with(response as TrackResponse) {
                    val favoriteTrackIds = appDatabase.favoriteTracksDao().getFavoriteIdList()
                    val data = results.map {
                        val trackId = it.trackId
                        val isFavorite = trackId in favoriteTrackIds

                        Track(
                            trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                            isFavorite
                        )
                    }
                    saveTracks(results)
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error("Server error"))
            }
        }
    }

    private suspend fun saveTracks(tracks: List<TrackDto>) {
        val trackEntities = tracks.map { trackDto ->
            tracksDbConvertor.map(trackDto).apply {
                addedAt = System.currentTimeMillis()
            }
        }

        val favoriteTracks = trackEntities.filter { it.isFavorite }
        appDatabase.favoriteTracksDao().insertTracks(favoriteTracks)
    }

}






