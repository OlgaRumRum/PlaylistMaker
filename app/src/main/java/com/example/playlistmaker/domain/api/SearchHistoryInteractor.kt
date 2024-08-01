package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getTrackHistory(): List<Track>
    fun addToTrackHistory(track: Track)
    fun clearTrackHistory()
}