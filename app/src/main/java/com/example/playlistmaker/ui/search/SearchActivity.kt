package com.example.playlistmaker.ui.search



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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity
import com.example.playlistmaker.util.Creator


class SearchActivity : AppCompatActivity() {


    private var text: String = ""

    private lateinit var binding: ActivitySearchBinding


    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var progressBar: ProgressBar

    private val searchRunnable = Runnable { search() }

    private val trackList = mutableListOf<Track>()

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            showMessage(StatusResponse.SUCCESS)
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            hideKeyboard()
            showHistoryMessage()
        }

        progressBar = binding.progressBar

        binding.searchRefreshButton.setOnClickListener {
            search()
            binding.searchErrorMessage.isVisible = false
        }


        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty()
                && searchHistoryInteractor.getTrackHistory().isNotEmpty()
            ) {
                showHistoryMessage()
            } else {
                showMessage(StatusResponse.SUCCESS)
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
                        showMessage(StatusResponse.SUCCESS)
                        searchDebounce()
                    }
                }
            })

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        searchAdapter = TrackAdapter()
        historyAdapter = TrackAdapter()

        searchAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            searchHistoryInteractor.addToTrackHistory(it)
            intentAudioPlayerActivity(it)
        }

        binding.rvTrack.layoutManager = LinearLayoutManager(this)
        searchAdapter.items = trackList
        binding.rvTrack.adapter = searchAdapter

        historyAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            searchHistoryInteractor.addToTrackHistory(it)
            historyAdapter.updateItems(searchHistoryInteractor.getTrackHistory())
            intentAudioPlayerActivity(it)
        }
        binding.historyList.layoutManager = LinearLayoutManager(this)
        binding.historyList.adapter = historyAdapter

        val historyTracks = searchHistoryInteractor.getTrackHistory()
        historyAdapter.updateItems(historyTracks)
    }

    private fun showHistoryMessage() {
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
        historyAdapter.updateItems(searchHistoryInteractor.getTrackHistory())
        binding.searchHistoryLayout.isVisible = historyAdapter.items.isNotEmpty()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(INPUT).toString()
    }

    private fun search() {
        val query = binding.inputEditText.text.toString()
        if (query.isNotEmpty()) {

            trackList.clear()
            searchAdapter.notifyDataSetChanged()

            binding.searchErrorMessage.isVisible = false
            binding.rvTrack.isVisible = false
            progressBar.isVisible = true

            Creator.provideTrackInteractor().search(query, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        progressBar.isVisible = false

                        when {
                            errorMessage != null -> {
                                showMessage(StatusResponse.ERROR)
                            }

                            foundTracks.isNullOrEmpty() -> {
                                showMessage(StatusResponse.EMPTY)
                            }

                            else -> {
                                trackList.clear()
                                trackList.addAll(foundTracks)
                                searchAdapter.notifyDataSetChanged()
                                showMessage(StatusResponse.SUCCESS)
                            }
                        }
                    }
                }
            })
            hideKeyboard()
        }
    }

    private fun showMessage(status: StatusResponse) {
        binding.apply {
            when (status) {
                StatusResponse.SUCCESS -> {
                    searchErrorMessage.isVisible = false
                    searchHistoryLayout.isVisible = false
                    rvTrack.isVisible = true
                }

                StatusResponse.EMPTY -> {
                    searchErrorText.text = getString(R.string.nothing_was_found)
                    searchErrorImage.setImageResource(R.drawable.search_error)
                    searchHistoryLayout.isVisible = false
                    rvTrack.isVisible = false
                    searchRefreshButton.isVisible = false
                    searchErrorMessage.isVisible = true
                }

                StatusResponse.ERROR -> {
                    searchErrorText.text = getString(R.string.no_interrnet_conection)
                    searchErrorImage.setImageResource(R.drawable.internet_error)
                    searchRefreshButton.isVisible = true
                    searchHistoryLayout.isVisible = false
                    rvTrack.isVisible = false
                    searchErrorMessage.isVisible = true
                }
            }
        }
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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    companion object {
        private const val INPUT = "INPUT"
        const val TRACK_KEY = "track_key"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}


