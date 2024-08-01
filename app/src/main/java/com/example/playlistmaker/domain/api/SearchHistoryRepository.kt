package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track


interface SearchHistoryRepository {
    fun getTrackHistory(): List<Track>
    fun addToTrackHistory(newTrack: Track)
    fun clearTrackHistory()

}