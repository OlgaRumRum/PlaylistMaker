package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

interface TracksRepository {
    fun search(expression: String): Resource<List<Track>>

}