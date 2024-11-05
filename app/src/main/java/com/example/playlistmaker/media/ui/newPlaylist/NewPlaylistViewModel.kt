package com.example.playlistmaker.media.ui.newPlaylist

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

/*class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _savePlaylistResult = MutableLiveData<Result<Unit>>()
    val savePlaylistResult: LiveData<Result<Unit>> = _savePlaylistResult


    fun savePlaylist(name: String, description: String? = null, coverPath: String? = null) {
        viewModelScope.launch {
            try {
                val playlist = Playlists(
                    name = name,
                    description = description,
                    coverPath = coverPath,
                    trackIds = emptyList(),
                    trackCount = 0
                )
                Log.d("NewPlaylistViewModel", "Saving playlist: $playlist") // Добавлено логирование
                playlistInteractor.addNewPlaylist(playlist)
                _savePlaylistResult.value = Result.success(Unit)
            } catch (e: Exception) {
                Log.e("NewPlaylistViewModel", "Error saving playlist", e) // Добавлено логирование
                _savePlaylistResult.value = Result.failure(e)
            }
        }
    }
}

 */
class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _savePlaylistResult = MutableLiveData<Result<Unit>>()
    val savePlaylistResult: LiveData<Result<Unit>> = _savePlaylistResult

    private val _playlistName = MutableLiveData<String>()
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData<String?>()
    val playlistDescription: LiveData<String?> = _playlistDescription

    private var _coverImageUri: Uri? = null
    fun setCoverImageUri(uri: Uri?) {
        _coverImageUri = uri
    }


    fun savePlaylist() {
        val name = _playlistName.value ?: ""
        val description = _playlistDescription.value

        viewModelScope.launch {
            try {
                val playlist = Playlist(
                    name = name,
                    description = description,
                    coverPath = _coverImageUri?.toString() ?: "", // Uri в строку или пустая строка
                    trackIds = emptyList(),
                    trackCount = 0
                )
                Log.d("NewPlaylistViewModel", "Saving playlist: $playlist")
                playlistInteractor.addNewPlaylist(playlist)
                _savePlaylistResult.value = Result.success(Unit)
            } catch (e: Exception) {
                Log.e("NewPlaylistViewModel", "Error saving playlist", e)
                _savePlaylistResult.value = Result.failure(e)
            }
        }
    }

    fun setPlaylistName(name: String) {
        _playlistName.value = name
    }

    fun setPlaylistDescription(description: String?) {
        _playlistDescription.value = description
    }
}




