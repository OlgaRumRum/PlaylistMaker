package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks_table")
data class PlaylistTrackEntity(
    @PrimaryKey
    val trackId: Long,
    val trackName: String = "",
    val artistName: String = "",
    val trackTimeMillis: Long = 0L,
    val artworkUrl100: String = "",
    val collectionName: String = "",
    val releaseDate: String? = "",
    val primaryGenreName: String = "",
    val country: String = "",
    val previewUrl: String? = ""
)
