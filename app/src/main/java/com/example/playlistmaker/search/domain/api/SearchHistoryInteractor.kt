package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {

    fun getTrackHistory(): List<Track>
    fun addToTrackHistory(track: Track)
    fun clearTrackHistory()
}