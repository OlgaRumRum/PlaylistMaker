package com.example.playlistmaker.media.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.main.MediaFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val playlistViewModel: PlaylistViewModel by viewModel()

    private var playlistAdapter: PlaylistAdapter? = null

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

        playlistViewModel.fillData()

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.mediaNewPlaylist.setOnClickListener {
            val action = MediaFragmentDirections.actionMediaFragmentToNewPlaylistFragment()
            findNavController().navigate(action)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty(state.message)
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


        binding.playlistRv.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistAdapter = PlaylistAdapter()
        binding.playlistRv.adapter = playlistAdapter

        playlistAdapter?.clear()
        playlistAdapter?.playlists?.addAll(playlists)
        playlistAdapter?.notifyDataSetChanged()
    }


    companion object {
        fun newInstance() = PlaylistFragment()

    }
}