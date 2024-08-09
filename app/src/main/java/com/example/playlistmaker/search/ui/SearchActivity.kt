package com.example.playlistmaker.search.ui



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.audioPlayer.ui.AudioPlayerActivity
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track


class SearchActivity : AppCompatActivity() {


    private var text: String = ""
    private var input = ""

    private lateinit var binding: ActivitySearchBinding


    private var searchAdapter = TrackAdapter()
    private var historyAdapter = TrackAdapter()


    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())


    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
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
            hideKeyboard()
        }



        binding.searchRefreshButton.setOnClickListener {
            viewModel.repeatRequest()
            viewModel.hideKeyboard()
        }


        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.changeInputEditTextState(hasFocus, binding.inputEditText.text.toString())
        }



        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }


        binding.inputEditText.addTextChangedListener(
            onTextChanged = { charSequence, _, _, _ ->
                viewModel.changeInputEditTextState(
                    binding.inputEditText.hasFocus(),
                    charSequence.toString()
                )
                input = charSequence.toString()
            }
        )



        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputEditText.text.toString())
                //true
            }
            false
        }


        binding.rvTrack.layoutManager = LinearLayoutManager(this)
        binding.historyList.layoutManager = LinearLayoutManager(this)

        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.SearchList -> showTracks(state.tracks)
                is SearchState.HistoryList -> showHistory(state.tracks)
                is SearchState.Error -> showError()
                is SearchState.Empty -> showEmptyMessage(state.emptyMessage)

            }
        }

        viewModel.isClearInputVisibile.observe(this) {
            binding.clearIcon.isVisible = it
        }


        searchAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener { track ->
            viewModel.addToHistory(track)
            intentAudioPlayerActivity(track)
        }


        historyAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener { track ->
            viewModel.addToHistory(track)
            intentAudioPlayerActivity(track)
        }

        viewModel.hideKeyboardEvent.observe(this, Observer {
            hideKeyboard()
        })
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        binding.progressBar.isVisible = false
        binding.rvTrack.isVisible = true
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false
        binding.rvTrack.adapter = searchAdapter
        searchAdapter.items = tracks

    }

    private fun showHistory(trackList: List<Track>) {
        binding.searchHistoryLayout.isVisible = true
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
        binding.historyList.isVisible = true
        binding.historyList.adapter = historyAdapter
        historyAdapter.items = trackList
    }

    private fun showError() {
        binding.searchErrorText.text = getString(R.string.no_interrnet_conection)
        binding.searchErrorImage.setImageResource(R.drawable.internet_error)
        binding.searchRefreshButton.isVisible = true
        binding.searchErrorMessage.isVisible = true
        binding.rvTrack.isVisible = false
        binding.progressBar.isVisible = false
        binding.searchHistoryLayout.isVisible = false
    }

    private fun showEmptyMessage(emptyMessage: String) {
        binding.searchErrorText.text = emptyMessage
        binding.searchErrorImage.setImageResource(R.drawable.search_error)
        binding.searchRefreshButton.isVisible = false
        binding.searchErrorMessage.isVisible = true
        binding.rvTrack.isVisible = false
        binding.progressBar.isVisible = false
        binding.searchHistoryLayout.isVisible = false
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


