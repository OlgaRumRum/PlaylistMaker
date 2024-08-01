package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getTrackHistory(): List<Track> {
        return searchHistoryRepository.getTrackHistory()
    }

    override fun addToTrackHistory(track: Track) {
        searchHistoryRepository.addToTrackHistory(track)
    }

    override fun clearTrackHistory() {
        searchHistoryRepository.clearTrackHistory()
    }
}