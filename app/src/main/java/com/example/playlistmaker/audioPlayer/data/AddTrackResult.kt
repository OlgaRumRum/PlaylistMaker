package com.example.playlistmaker.audioPlayer.data

sealed class AddTrackResult {
    data class Success(val playlistName: String) : AddTrackResult()
    data class DuplicateTrack(val playlistName: String) : AddTrackResult()
}