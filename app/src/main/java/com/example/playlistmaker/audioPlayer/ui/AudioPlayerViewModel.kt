package com.example.playlistmaker.audioPlayer.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioPlayer.domain.models.TrackInfo
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    private val _trackInfo = MutableLiveData(TrackInfo())
    val trackInfo: LiveData<TrackInfo> get() = _trackInfo

    private val _playbackState = MutableLiveData<Int>()
    val playbackState: LiveData<Int> get() = _playbackState

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (audioPlayerInteractor.isPlaying()) {
                val formattedTime = dateFormat.format(audioPlayerInteractor.getPlaybackPosition())
                _trackInfo.value = _trackInfo.value?.copy(currentPosition = formattedTime)
                handler.postDelayed(this, TIMER_UPDATE_INTERVAL)
            }
        }
    }.also {
        handler.post(it)
    }

    fun preparePlayer(trackUrl: String) {
        audioPlayerInteractor.prepareTrack(
            trackUrl,
            onPrepared = {
                _playbackState.value = STATE_PREPARED
            },
            onCompletion = {
                _playbackState.value = STATE_COMPLETED
                resetTrackInfo()
                stopProgressUpdates()
            }
        )
    }

    fun startPlayer() {
        audioPlayerInteractor.startPlayback()
        _playbackState.value = STATE_PLAYING
        handler.post(progressRunnable)
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayback()
        _playbackState.value = STATE_PAUSED
        stopProgressUpdates()
    }


    fun releasePlayer() {
        audioPlayerInteractor.releasePlayer()
        stopProgressUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun resetTrackInfo() {
        _trackInfo.value = TrackInfo(currentPosition = "00:00")
    }

    private fun stopProgressUpdates() {
        progressRunnable.let { handler.removeCallbacks(it) }
    }

    companion object {
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETED = 4

        private const val TIMER_UPDATE_INTERVAL = 300L

        fun getViewModelFactory(interactor: AudioPlayerInteractor): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    AudioPlayerViewModel(interactor)
                }
            }
    }
}
