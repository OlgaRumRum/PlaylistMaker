package com.example.playlistmaker


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {


    private var text: String = ""

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)

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


    private lateinit var searchHistory: SearchHistory

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val trackList = ArrayList<Track>()



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
            hideKeyboard(inputEditText)
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


        searchRefreshButton.setOnClickListener {
            search()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()
                && searchHistory.getTrackHistory().isNotEmpty()
            ) {
                showHistoryMessage()
            } else {
                showMessage(StatusResponse.SUCCESS)
            }
        }


        buttonClearHistory.setOnClickListener {
            searchHistory.clearTrackHistory()
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
            Toast.makeText(
                this@SearchActivity,
                "Track: " + item.artistName + " - " + item.trackName,
                Toast.LENGTH_SHORT
            ).show()
        }
        historyAdapter = TrackAdapter(onHistoryItemClickListener)
        historyList.layoutManager = LinearLayoutManager(this)
        historyList.adapter = historyAdapter


        searchHistory = SearchHistory(this, historyAdapter)

        val onItemClickListener = ItemClickListener { item ->
            searchHistory.addToTrackHistory(item)
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

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun search() {
        trackService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>, response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            showMessage(StatusResponse.SUCCESS)
                            trackList.addAll(response.body()?.results!!)
                            searchAdapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            showMessage(StatusResponse.EMPTY)
                        }
                    } else {
                        showMessage(StatusResponse.ERROR)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(StatusResponse.ERROR)

                }

            })
    }

    private fun showMessage(status: StatusResponse) {
        searchErrorMessage.isVisible = true
        trackList.clear()
        searchAdapter.notifyDataSetChanged()
        when (status) {
            StatusResponse.SUCCESS -> {
                searchErrorMessage.isVisible = false
                searchHistoryLayout.isVisible = false
                searchList.isVisible = true
            }

            StatusResponse.EMPTY -> {
                searchErrorText.text = getString(R.string.nothing_was_found)
                searchErrorImage.setImageResource(R.drawable.search_error)
                searchHistoryLayout.isVisible = false
                searchHistoryLayout.isVisible = false
                searchList.isVisible = false
            }

            StatusResponse.ERROR -> {
                searchErrorText.text = getString(R.string.no_interrnet_conection)
                searchErrorImage.setImageResource(R.drawable.internet_error)
                searchRefreshButton.isVisible = true
                searchHistoryLayout.isVisible = false
                searchList.isVisible = false
            }
        }
    }


    companion object {
        private const val INPUT = "INPUT"
    }
}