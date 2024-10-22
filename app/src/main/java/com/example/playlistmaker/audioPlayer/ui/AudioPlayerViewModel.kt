package com.example.playlistmaker.audioPlayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioPlayer.domain.models.TrackInfo
import com.example.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val dateFormat by lazy {
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    }

    private val _trackInfo = MutableLiveData(TrackInfo())
    val trackInfo: LiveData<TrackInfo> get() = _trackInfo

    private val _playbackState = MutableLiveData<Int>(STATE_DEFAULT)
    val playbackState: LiveData<Int> get() = _playbackState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private var job: Job? = null

    init {
        startProgressUpdates()
    }


    fun checkIfTrackIsFavorite(trackId: Int) {
        viewModelScope.launch {
            val favoriteStatus = favoriteTracksInteractor.isTrackFavorite(trackId)
            _isFavorite.postValue(favoriteStatus)
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoriteTracksInteractor.removeTrackFromFavorites(track)
                _isFavorite.postValue(false)
            } else {
                favoriteTracksInteractor.addTrackToFavorites(track)
                _isFavorite.postValue(true)
            }
        }
    }


    fun preparePlayer(trackUrl: String) {
        if (playbackState.value == STATE_DEFAULT)
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
        startProgressUpdates()
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

    private fun startProgressUpdates() {
        stopProgressUpdates()

        job = viewModelScope.launch {
            while (audioPlayerInteractor.isPlaying()) {
                val currentPosition = audioPlayerInteractor.getPlaybackPosition()
                val formattedTime = dateFormat.format(currentPosition)
                _trackInfo.value = _trackInfo.value?.copy(currentPosition = formattedTime)
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    private fun stopProgressUpdates() {
        job?.cancel()
        job = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun formatReleaseDate(releaseDate: String?): String {
        return releaseDate?.substring(0, 4) ?: "Unknown"
    }

    fun formatArtworkUrl(artworkUrl: String?): String? {
        return artworkUrl?.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun resetTrackInfo() {
        _trackInfo.value = TrackInfo(currentPosition = "00:00")
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETED = 4

        private const val TIMER_UPDATE_INTERVAL = 300L
        private const val DATE_FORMAT = "mm:ss"
    }
}
