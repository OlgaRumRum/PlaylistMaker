package com.example.playlistmaker.search.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var searchJob: Job? = null

    private var latestSearchText: String = ""

    val trackList = mutableListOf<Track>()

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> get() = _state

    private val _isClearInputVisibile = MutableLiveData<Boolean>(false)
    val isClearInputVisibile: LiveData<Boolean> get() = _isClearInputVisibile


    private fun renderState(state: SearchState) {
        _state.postValue(state)
    }

    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                searchInteractor.search(newSearchText).collect { (foundTracks, errorMessage) ->
                    if (foundTracks != null) {
                        trackList.clear()
                        trackList.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(SearchState.Error(errorMessage = errorMessage))
                        }
                        trackList.isEmpty() -> {
                            renderState(SearchState.Empty(emptyMessage = "Nothing was found"))
                        }
                        else -> {
                            renderState(SearchState.SearchList(tracks = trackList))
                        }
                    }
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }


    private fun searchDebounce(changedText: String) {
        latestSearchText = changedText

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            if (latestSearchText == changedText) {
                search(latestSearchText)
            }
        }
    }

    fun changeInputEditTextState(focus: Boolean, input: String) {
        val searchHistory = searchHistoryInteractor.getTrackHistory()
        _isClearInputVisibile.value = input.isNotEmpty()
        if (focus && input.isEmpty() && searchHistory.isNotEmpty()) {
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
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

}

