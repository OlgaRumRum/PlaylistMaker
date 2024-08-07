package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchRepository {
    fun search(expression: String): Resource<List<Track>>

}