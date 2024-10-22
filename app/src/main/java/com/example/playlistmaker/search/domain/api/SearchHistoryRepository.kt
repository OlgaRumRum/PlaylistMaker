package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track


interface SearchHistoryRepository {
    fun getTrackHistory(): MutableList<Track>
    fun addToTrackHistory(newTrack: Track)
    fun clearTrackHistory()

}