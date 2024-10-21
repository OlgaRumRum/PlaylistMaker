package com.example.playlistmaker.search.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.audioPlayer.ui.AudioPlayerActivity
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {


    private var text: String = ""
    private var input = ""


    private var _binding: FragmentSearchBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding is not initialized" }


    private var searchAdapter = TrackAdapter()
    private var historyAdapter = TrackAdapter()

    private var isClickAllowed = true

    private lateinit var textWatcher: TextWatcher


    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            searchAdapter.clear()
            hideKeyboard()
        }



        binding.searchRefreshButton.setOnClickListener {
            viewModel.repeatRequest()
        }


        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.changeInputEditTextState(hasFocus, binding.inputEditText.text.toString())
        }



        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }


        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.changeInputEditTextState(
                    binding.inputEditText.hasFocus(),
                    s.toString()
                )
                input = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }


        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputEditText.text.toString())
            }
            false
        }


        binding.rvTrack.layoutManager = LinearLayoutManager(requireContext())
        binding.historyList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.SearchList -> showTracks(state.tracks)
                is SearchState.HistoryList -> showHistory(state.tracks)
                is SearchState.Error -> showError()
                is SearchState.Empty -> showEmptyMessage(state.emptyMessage)
                is SearchState.NoHistory -> showNoHistoryMessage()

            }
        }

        viewModel.isClearInputVisibile.observe(viewLifecycleOwner) {
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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
    }


    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false
    }

    private fun showTracks(tracks: MutableList<Track>) {
        binding.progressBar.isVisible = false
        binding.rvTrack.isVisible = true
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false
        binding.rvTrack.adapter = searchAdapter
        searchAdapter.items = tracks

    }

    private fun showHistory(trackList: MutableList<Track>) {
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

    private fun showNoHistoryMessage() {
        binding.rvTrack.isVisible = false
        binding.searchErrorMessage.isVisible = false
        binding.searchHistoryLayout.isVisible = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT, text)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            text = it.getString(INPUT, "")
        }
    }

    private fun hideKeyboard() {
        val view =
            requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun intentAudioPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            startActivity(intent)
        }
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val INPUT = "INPUT"
        const val TRACK_KEY = "track_key"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}


