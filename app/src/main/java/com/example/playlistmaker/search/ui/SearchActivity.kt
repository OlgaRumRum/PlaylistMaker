package com.example.playlistmaker.search.ui



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.audioPlayer.ui.AudioPlayerActivity
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track


class SearchActivity : AppCompatActivity() {


    private var text: String = ""

    private lateinit var binding: ActivitySearchBinding

    private val searchInteractor = Creator.provideTrackInteractor()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var progressBar: ProgressBar


    private val trackList = mutableListOf<Track>()

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())


    private val viewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(
                searchInteractor,
                searchHistoryInteractor
            )
        )[SearchViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            showListMessage()
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            hideKeyboard()
            showHistoryMessage()
        }

        progressBar = binding.progressBar

        binding.searchRefreshButton.setOnClickListener {
            viewModel.search(binding.inputEditText.text.toString())
            binding.searchErrorMessage.isVisible = false
            viewModel.hideKeyboard()
        }


        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty()
                && searchHistoryInteractor.getTrackHistory().isNotEmpty()
            ) {
                showHistoryMessage()
            } else {
                showListMessage()
            }
        }


        binding.buttonClearHistory.setOnClickListener {
            searchHistoryInteractor.clearTrackHistory()
            searchAdapter.notifyDataSetChanged()
            binding.searchHistoryLayout.isVisible = false
        }


        binding.inputEditText.addTextChangedListener(
            onTextChanged = { charSequence, _, _, _ ->
                text = binding.inputEditText.text.toString()

                binding.clearIcon.isVisible = !charSequence.isNullOrEmpty()

                if (charSequence != null) {
                    if (binding.inputEditText.hasFocus() && charSequence.isEmpty()) {
                        showHistoryMessage()
                    } else {
                        showListMessage()
                        viewModel.searchDebounce(changedText = charSequence.toString())
                    }
                }
            })

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputEditText.text.toString())
                true
            }
            false
        }



        searchAdapter = TrackAdapter()
        historyAdapter = TrackAdapter()



        binding.rvTrack.layoutManager = LinearLayoutManager(this)
        searchAdapter.items = trackList
        binding.rvTrack.adapter = searchAdapter

        binding.historyList.layoutManager = LinearLayoutManager(this)
        binding.historyList.adapter = historyAdapter

        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.SearchList -> showTracks(state.tracks)
                is SearchState.HistoryList -> showHistory(state.tracks)
                is SearchState.Error -> showError()
                is SearchState.Empty -> showEmptyMessage(state.emptyMessage)

            }
        }

        viewModel.historyState.observe(this) { historyTracks ->
            historyAdapter.updateItems(historyTracks)
            binding.searchHistoryLayout.isVisible = historyTracks.isNotEmpty()
        }


        searchAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            searchHistoryInteractor.addToTrackHistory(it)
            intentAudioPlayerActivity(it)
        }


        historyAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener { track ->
            viewModel.addToHistory(track)
            intentAudioPlayerActivity(track)
            historyAdapter.updateItems(searchHistoryInteractor.getTrackHistory())
        }


        val historyTracks = searchHistoryInteractor.getTrackHistory()
        historyAdapter.updateItems(historyTracks)


        viewModel.hideKeyboardEvent.observe(this, Observer {
            hideKeyboard()
        })
    }

    private fun showLoading() {
        progressBar.isVisible = true
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        trackList.clear()
        trackList.addAll(tracks)
        searchAdapter.notifyDataSetChanged()

        progressBar.isVisible = false
        binding.rvTrack.isVisible = true
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false

    }

    private fun showHistory(tracks: List<Track>) {

        historyAdapter.updateItems(tracks)
        binding.searchHistoryLayout.isVisible = true
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
    }

    private fun showError() {
        binding.searchErrorText.text = getString(R.string.no_interrnet_conection)
        binding.searchErrorImage.setImageResource(R.drawable.internet_error)
        binding.searchRefreshButton.isVisible = true
        binding.searchErrorMessage.isVisible = true
        binding.rvTrack.isVisible = false
        progressBar.isVisible = false
        binding.searchHistoryLayout.isVisible = false
    }

    private fun showEmptyMessage(emptyMessage: String) {
        binding.searchErrorText.text = emptyMessage
        binding.searchErrorImage.setImageResource(R.drawable.search_error)
        binding.searchRefreshButton.isVisible = false
        binding.searchErrorMessage.isVisible = true
        binding.rvTrack.isVisible = false
        progressBar.isVisible = false
        binding.searchHistoryLayout.isVisible = false
    }

    private fun showHistoryMessage() {
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
        historyAdapter.updateItems(searchHistoryInteractor.getTrackHistory())
        binding.searchHistoryLayout.isVisible = historyAdapter.items.isNotEmpty()

    }

    private fun showListMessage() {
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false
        binding.rvTrack.isVisible = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(INPUT).toString()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun intentAudioPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val INPUT = "INPUT"
        const val TRACK_KEY = "track_key"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}


