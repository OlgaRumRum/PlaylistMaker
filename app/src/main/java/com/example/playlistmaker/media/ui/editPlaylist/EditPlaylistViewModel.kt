package com.example.playlistmaker.media.ui.editPlaylist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch


class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlistName = MutableLiveData<String>()
    val playlistName: LiveData<String> get() = _playlistName

    private val _playlistDescription = MutableLiveData<String?>()
    val playlistDescription: LiveData<String?> get() = _playlistDescription

    private val _coverImageUri = MutableLiveData<Uri?>()
    val coverImageUri: LiveData<Uri?> get() = _coverImageUri

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

}
