package com.example.playlistmaker.media.ui.favorite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.audioPlayer.ui.AudioPlayerActivity
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.TrackViewHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private var adapter: TrackAdapter? = null

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()
    private var isClickAllowed = true

    private var audioPlayerLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter()


        adapter!!.onItemClickListener = TrackViewHolder.OnItemClickListener { track ->
            intentAudioPlayerActivity(track)
        }

        binding.favoriteList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoriteList.adapter = adapter

        audioPlayerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                favoriteTracksViewModel.fillData()
            }
        }

        favoriteTracksViewModel.fillData()

        favoriteTracksViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }


    private fun intentAudioPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            audioPlayerLauncher?.launch(intent)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.tracks)
            is FavoriteState.Empty -> showEmpty(state.message)
            is FavoriteState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        binding.favoriteList.isVisible = false
        binding.mediaPlaceholderIv.isVisible = false
        binding.mediaPlaceholderTv.isVisible = false
        binding.progressBar.isVisible = true

    }

    private fun showEmpty(message: String) {
        binding.favoriteList.isVisible = false
        binding.mediaPlaceholderIv.isVisible = true
        binding.mediaPlaceholderTv.isVisible = true
        binding.progressBar.isVisible = false

        binding.mediaPlaceholderTv.text = message
    }

    private fun showContent(tracks: List<Track>) {
        binding.favoriteList.isVisible = true
        binding.mediaPlaceholderIv.isVisible = false
        binding.mediaPlaceholderTv.isVisible = false
        binding.progressBar.isVisible = false

        adapter?.clear()
        adapter?.items?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }


    companion object {
        fun newInstance() = FavoriteTracksFragment()
        const val TRACK_KEY = "track_key"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }
}