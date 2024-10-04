package com.example.playlistmaker.audioPlayer.domain.impl

import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerRepository

class AudioPlayerInteractorImpl(private val audioPlayerRepository: AudioPlayerRepository) :
    AudioPlayerInteractor {

    override suspend fun prepareTrack(
        trackUrl: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        audioPlayerRepository.prepare(trackUrl, onPrepared, onCompletion)
    }

    override fun startPlayback() {
        audioPlayerRepository.start()
    }

    override fun pausePlayback() {
        audioPlayerRepository.pause()
    }

    override fun getPlaybackPosition(): Int {
        return audioPlayerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return audioPlayerRepository.isPlaying()
    }

    override fun releasePlayer() {
        audioPlayerRepository.release()
    }


}
