package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PlaylistDbConverter {
    private val gson = Gson()

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val trackListType = object : TypeToken<List<Long>>() {}.type
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = gson.fromJson(entity.trackIds, trackListType),
            trackCount = entity.trackCount
        )
    }
}



