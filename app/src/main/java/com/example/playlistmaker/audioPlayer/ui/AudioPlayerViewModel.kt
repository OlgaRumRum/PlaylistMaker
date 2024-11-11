package com.example.playlistmaker.audioPlayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.audioPlayer.data.AddTrackResult
import com.example.playlistmaker.audioPlayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioPlayer.domain.models.TrackInfo
import com.example.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
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

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists


    private val _currentPlaylistName = MutableLiveData<String?>()
    val currentPlaylistName: LiveData<String?> = _currentPlaylistName

    private val _addTrackResult = MutableLiveData<AddTrackResult>()
    val addTrackResult: LiveData<AddTrackResult> = _addTrackResult

    private val _selectedTrack = MutableLiveData<Track?>(null)
    val selectedTrack: LiveData<Track?> = _selectedTrack


    fun setSelectedTrack(track: Track?) {
        _selectedTrack.value = track
    }


    private var job: Job? = null

    init {
        startProgressUpdates()
    }


    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            val isInPlaylist = isTrackInPlaylist(playlist, track)
            try {
                if (!isInPlaylist) {
                    val updatedPlaylist = playlist.copy(
                        trackIds = playlist.trackIds + listOf(track.trackId),
                        trackCount = playlist.trackIds.size + 1
                    )
                    playlistInteractor.addTrackToPlaylist(updatedPlaylist, track)
                    _addTrackResult.value = AddTrackResult.Success(playlist.name)
                } else {
                    _addTrackResult.value = AddTrackResult.DuplicateTrack(playlist.name)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun isTrackInPlaylist(playlist: Playlist, track: Track): Boolean {
        return playlist.trackIds.contains(track.trackId)
    }


    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                playlistInteractor.getPlaylistsFlow().collect { playlists ->
                    _playlists.postValue(playlists)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }


    fun checkIfTrackIsFavorite(trackId: Long) {
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
