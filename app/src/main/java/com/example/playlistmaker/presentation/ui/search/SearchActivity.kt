package com.example.playlistmaker.presentation.ui.search


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.audioPlayer.AudioPlayerActivity


class SearchActivity : AppCompatActivity() {


    private var text: String = ""

    private lateinit var searchList: RecyclerView
    private lateinit var searchErrorImage: ImageView
    private lateinit var searchErrorText: TextView
    private lateinit var searchRefreshButton: Button
    private lateinit var searchErrorMessage: LinearLayout
    private lateinit var inputEditText: EditText


    private lateinit var hintMessage: TextView
    private lateinit var historyList: RecyclerView
    private lateinit var buttonClearHistory: Button
    private lateinit var searchHistoryLayout: LinearLayout

    private lateinit var searchHistoryRepository: SearchHistoryRepository

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var progressBar: ProgressBar

    private val searchRunnable = Runnable { search() }

    private val trackList = ArrayList<Track>()

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbarSearch = findViewById<Toolbar>(R.id.toolbar_search)

        toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            showMessage(StatusResponse.SUCCESS)
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            hideKeyboard()
            showHistoryMessage()
        }

        searchList = findViewById(R.id.rvTrack)
        searchErrorImage = findViewById(R.id.searchErrorImage)
        searchErrorText = findViewById(R.id.searchErrorText)
        searchRefreshButton = findViewById(R.id.searchRefreshButton)
        searchErrorMessage = findViewById(R.id.searchErrorMessage)
        inputEditText = findViewById(R.id.inputEditText)


        hintMessage = findViewById(R.id.hintMessage)
        historyList = findViewById(R.id.historyList)
        buttonClearHistory = findViewById(R.id.buttonClearHistory)
        searchHistoryLayout = findViewById(R.id.SearchHistoryLayout)

        progressBar = findViewById(R.id.progressBar)

        searchRefreshButton.setOnClickListener {
            search()
            searchErrorMessage.isVisible = false
        }


        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()
                && searchHistoryRepository.getTrackHistory().isNotEmpty()
            ) {
                showHistoryMessage()
            } else {
                showMessage(StatusResponse.SUCCESS)
            }
        }


        buttonClearHistory.setOnClickListener {
            searchHistoryRepository.clearTrackHistory()
            searchAdapter.items.clear()
            searchAdapter.notifyDataSetChanged()
            searchHistoryLayout.isVisible = false

        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = inputEditText.text.toString()
                clearButton.visibility = clearButtonVisibility(s)
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    showHistoryMessage()
                } else {
                    showMessage(StatusResponse.SUCCESS)
                    searchDebounce()
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }


            private fun clearButtonVisibility(s: CharSequence?): Int {
                return if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        val onHistoryItemClickListener = ItemClickListener { item ->
            intentAudioPlayerActivity(item)
        }
        historyAdapter = TrackAdapter(onHistoryItemClickListener)
        historyList.layoutManager = LinearLayoutManager(this)
        historyList.adapter = historyAdapter


        Creator.init(this, historyAdapter)
        searchHistoryRepository = Creator.provideSearchHistoryRepository()

        val onItemClickListener = ItemClickListener { item ->
            searchHistoryRepository.addToTrackHistory(item)
            intentAudioPlayerActivity(item)
        }

        searchList.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(onItemClickListener)
        searchAdapter.items = trackList
        searchList.adapter = searchAdapter
    }

    private fun showHistoryMessage() {
        searchList.isVisible = false
        searchErrorMessage.isVisible = false
        searchHistoryLayout.isVisible = historyAdapter.items.isNotEmpty()
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
        inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }


    private fun search() {
        val query = inputEditText.text.toString()
        if (query.isNotEmpty()) {
            searchErrorMessage.isVisible = false
            searchList.isVisible = false
            progressBar.isVisible = true


            if (!isNetworkAvailable()) {
                progressBar.isVisible = false
                showMessage(StatusResponse.ERROR)
                hideKeyboard()
                return
            }
            Creator.provideTrackInteractor().search(query, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE

                        if (foundTracks.isNotEmpty()) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            searchAdapter.notifyDataSetChanged()
                            showMessage(StatusResponse.SUCCESS)
                        } else {
                            showMessage(StatusResponse.EMPTY)
                        }

                    }
                }

                override fun onFailure(t: Throwable) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        showMessage(StatusResponse.ERROR)
                    }
                }

            })

            hideKeyboard()

        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showMessage(status: StatusResponse) {
        when (status) {
            StatusResponse.SUCCESS -> {
                searchErrorMessage.isVisible = false
                searchHistoryLayout.isVisible = false
                searchList.isVisible = true
                searchErrorMessage.isVisible = false
            }

            StatusResponse.EMPTY -> {
                searchErrorText.text = getString(R.string.nothing_was_found)
                searchErrorImage.setImageResource(R.drawable.search_error)
                searchHistoryLayout.isVisible = false
                searchList.isVisible = false
                searchRefreshButton.isVisible = false
                searchErrorMessage.isVisible = true
            }

            StatusResponse.ERROR -> {
                searchErrorText.text = getString(R.string.no_interrnet_conection)
                searchErrorImage.setImageResource(R.drawable.internet_error)
                searchRefreshButton.isVisible = true
                searchHistoryLayout.isVisible = false
                searchList.isVisible = false
                searchErrorMessage.isVisible = true
            }
        }
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


