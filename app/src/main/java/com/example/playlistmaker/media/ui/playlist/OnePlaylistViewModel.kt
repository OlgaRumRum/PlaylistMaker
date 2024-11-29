package com.example.playlistmaker.media.ui.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.durationTextFormater
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class OnePlaylistViewModel(
    private val context: Context,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> get() = _playlist

    private val _totalDuration = MutableLiveData<String>()
    val totalDuration: LiveData<String> get() = _totalDuration

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _trackCount = MutableLiveData<Int>()
    val trackCount: LiveData<Int> get() = _trackCount


    fun loadPlaylistById(playlistId: Long) {
        viewModelScope.launch {
            _playlist.value = playlistInteractor.getPlaylistById(playlistId)
            _playlist.value?.let { playlist ->
                loadTracks(playlist.trackIds)
            }
        }
    }

    private fun loadTracks(trackIds: List<Long>) {
        viewModelScope.launch {
            val tracks = playlistInteractor.getTracks(trackIds)
            _tracks.value = tracks
            _trackCount.value = tracks.size
            _totalDuration.value = getTotalDuration(tracks)
        }
    }

    fun removeTrack(trackId: Long, playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(trackId, playlistId)
            playlistInteractor.getPlaylistById(playlistId)?.let { loadTracks(it.trackIds) }
        }
    }

    private fun getTotalDuration(tracks: List<Track>): String {
        val durationMillis = tracks.sumOf { it.trackTimeMillis }
        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
        return context.durationTextFormater(durationMinutes.toInt())
    }

    fun removePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

}

