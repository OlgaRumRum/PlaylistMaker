package com.example.playlistmaker.audioPlayer.domain.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize


data class TrackInfo(
    val artistName: String = "",
    val artworkUrl100: String = "",
    val collectionName: String = "",
    val country: String = "",
    val previewUrl: String = "",
    val primaryGenreName: String = "",
    val releaseDate: String = "",
    val trackName: String = "",
    val trackTimeMillis: Long = 0L,
    val currentPosition: String = "00:00"
) : Parcelable

