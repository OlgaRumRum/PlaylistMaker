package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(expression: String): Flow<Resource<List<Track>>>
}