package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getTrackHistory(): MutableList<Track> {
        return searchHistoryRepository.getTrackHistory()
    }

    override fun addToTrackHistory(track: Track) {
        searchHistoryRepository.addToTrackHistory(track)
    }

    override fun clearTrackHistory() {
        searchHistoryRepository.clearTrackHistory()
    }
}