package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_tracks_table")
class TracksEntity(
    @PrimaryKey
    val trackId: Int,
    val trackName: String = "",
    val artistName: String = "",
    val trackTimeMillis: Long = 0L,
    val artworkUrl100: String = "",
    val collectionName: String = "",
    val releaseDate: String? = "",
    val primaryGenreName: String = "",
    val country: String = "",
    val previewUrl: String? = "",
    var isFavorite: Boolean = false,
    var addedAt: Long = System.currentTimeMillis()

)
