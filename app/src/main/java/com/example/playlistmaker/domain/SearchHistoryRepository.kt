package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track


interface SearchHistoryRepository {
    fun getTrackHistory(): List<Track>
    fun addToTrackHistory(track: Track)
    fun clearTrackHistory()

    fun saveTrackHistory(tracks: List<Track>)
}