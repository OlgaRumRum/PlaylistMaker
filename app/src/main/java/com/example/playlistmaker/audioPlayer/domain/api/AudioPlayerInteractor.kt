package com.example.playlistmaker.audioPlayer.domain.api


interface AudioPlayerInteractor {
    fun prepareTrack(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayback()
    fun pausePlayback()
    fun getPlaybackPosition(): Int
    fun isPlaying(): Boolean
    fun releasePlayer()


}
