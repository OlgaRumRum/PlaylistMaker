package com.example.playlistmaker.media.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.main.MediaFragmentDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val playlistViewModel: PlaylistsViewModel by viewModel()

    private var playlistAdapter: PlaylistsAdapter? = null

    private var isClickAllowed = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClickAllowed = true

        playlistViewModel.fillData()

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.mediaNewPlaylist.setOnClickListener {
            val action = MediaFragmentDirections.actionMediaFragmentToNewPlaylistFragment(null)
            findNavController().navigate(action)
        }

        binding.playlistRv.layoutManager = GridLayoutManager(requireContext(), 2)

        playlistAdapter = PlaylistsAdapter().apply {
            onItemClickListener = PlaylistsViewHolder.OnItemClickListener { playlist ->
                intentOnePlaylistFragment(playlist)
            }
        }
        binding.playlistRv.adapter = playlistAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty(state.message)
        }
    }


    private fun showEmpty(message: String) {
        binding.playlistRv.isVisible = false
        binding.mediaPlaceholderIv.isVisible = true
        binding.mediaPlaceholderTv.isVisible = true

        binding.mediaPlaceholderTv.text = message
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.playlistRv.isVisible = true
        binding.mediaPlaceholderIv.isVisible = false
        binding.mediaPlaceholderTv.isVisible = false

        playlistAdapter?.clear()
        playlistAdapter?.playlists?.addAll(playlists)
        playlistAdapter?.notifyDataSetChanged()
    }

    private fun intentOnePlaylistFragment(playlist: Playlist) {
        if (clickDebounce()) {
            val bundle = Bundle().apply {
                putLong(PLAYLIST_KEY, playlist.id)
            }
            findNavController().navigate(R.id.action_mediaFragment_to_onePlaylistFragment, bundle)
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
        fun newInstance() = PlaylistsFragment()
        const val PLAYLIST_KEY = "playlist_key"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }
}