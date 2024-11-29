package com.example.playlistmaker

object FormatDuration {

    fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / 1000) / 60
        return String.format("%d:%02d", minutes, seconds)
    }
}