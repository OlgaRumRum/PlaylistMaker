package com.example.playlistmaker.search.ui


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track


class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable {
        val newSearchText = latestSearchText
        search(newSearchText)
    }

    private var latestSearchText: String = ""

    val trackList = mutableListOf<Track>()

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> get() = _state

    private val _isClearInputVisibile = MutableLiveData<Boolean>(false)
    val isClearInputVisibile: LiveData<Boolean> get() = _isClearInputVisibile


    private val _hideKeyboardEvent = MutableLiveData<Unit>()
    val hideKeyboardEvent: LiveData<Unit> get() = _hideKeyboardEvent


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


    fun searchDebounce(changedText: String) {
        latestSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        hideKeyboard()
    }


    fun changeInputEditTextState(focus: Boolean, input: String) {
        val searchHistory = searchHistoryInteractor.getTrackHistory()
        _isClearInputVisibile.value = input.isNotEmpty()
        if (focus && input.isEmpty() && searchHistory.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
            _state.value = SearchState.HistoryList(searchHistory)
        } else {
            searchDebounce(input)
        }
    }


    fun addToHistory(track: Track) {
        searchHistoryInteractor.addToTrackHistory(track)
        if (_state.value is SearchState.HistoryList) {
            _state.value =
                SearchState.HistoryList(searchHistoryInteractor.getTrackHistory())
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearTrackHistory()
        _state.value = SearchState.SearchList(emptyList())
    }

    fun repeatRequest() {
        search(latestSearchText)
    }


    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

}