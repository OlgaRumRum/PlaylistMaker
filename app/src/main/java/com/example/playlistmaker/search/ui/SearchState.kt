package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    data object Loading : SearchState
    data class SearchList(val tracks: MutableList<Track>) : SearchState
    data class HistoryList(val tracks: MutableList<Track>) : SearchState
    data class Error(val errorMessage: String) : SearchState
    data class Empty(val emptyMessage: String) : SearchState
    data object NoHistory : SearchState


}