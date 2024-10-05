package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface SearchInteractor {
    fun search(expression: String): Flow<Pair<List<Track>?, String?>>

}
