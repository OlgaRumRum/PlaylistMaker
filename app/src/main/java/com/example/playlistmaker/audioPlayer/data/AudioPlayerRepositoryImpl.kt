package com.example.playlistmaker.audioPlayer.data

import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerRepository

class AudioPlayerRepositoryImpl() : AudioPlayerRepository {

    private val audioPlayer = AudioPlayerImpl()

    override fun prepare(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        audioPlayer.prepare(trackUrl, onPrepared, onCompletion)
    }

    override fun start() {
        audioPlayer.start()
    }

    override fun pause() {
        audioPlayer.pause()
    }

    override fun reset() {
        audioPlayer.reset()
    }

    override fun release() {
        audioPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return audioPlayer.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.getCurrentPosition()
    }

}