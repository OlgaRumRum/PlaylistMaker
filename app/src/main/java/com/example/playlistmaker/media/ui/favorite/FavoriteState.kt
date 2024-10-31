package com.example.playlistmaker.media.ui.favorite

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteState {
    object Loading : FavoriteState

    data class Content(
        val tracks: List<Track>
    ) : FavoriteState

    data class Empty(
        val message: String
    ) : FavoriteState
}