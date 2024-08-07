package com.example.playlistmaker.search.ui


import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track


class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())


    private var latestSearchText: String? = null

    val trackList = mutableListOf<Track>()

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> get() = _state


    private val _historyState = MutableLiveData<List<Track>>()
    val historyState: LiveData<List<Track>> = _historyState

    private val _hideKeyboardEvent = MutableLiveData<Unit>()
    val hideKeyboardEvent: LiveData<Unit> get() = _hideKeyboardEvent


    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { search(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
        hideKeyboard()
    }

    private fun renderState(state: SearchState) {
        _state.postValue(state)
    }

    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            searchInteractor.search(newSearchText, object : SearchInteractor.SearchConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    if (foundTracks != null) {
                        trackList.clear()
                        trackList.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {

                            renderState(
                                SearchState.Error(errorMessage = errorMessage)
                            )

                        }

                        trackList.isEmpty() -> {
                            renderState(
                                SearchState.Empty(
                                    emptyMessage = "Nothing was found"
                                )
                            )
                        }

                        else -> {
                            renderState(SearchState.SearchList(tracks = trackList))
                        }
                    }

                }
            })
        }
    }

    fun hideKeyboard() {
        _hideKeyboardEvent.postValue(Unit)
    }


    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    private fun updateHistory() {
        _historyState.value = searchHistoryInteractor.getTrackHistory()
    }

    fun addToHistory(track: Track) {
        searchHistoryInteractor.addToTrackHistory(track)
        updateHistory()
    }


    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        fun getViewModelFactory(
            searchInteractor: SearchInteractor,
            searchHistoryInteractor: SearchHistoryInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(searchInteractor, searchHistoryInteractor)
            }
        }
    }

}