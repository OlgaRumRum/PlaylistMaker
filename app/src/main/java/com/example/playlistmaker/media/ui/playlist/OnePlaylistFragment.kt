package com.example.playlistmaker.media.ui.playlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.FormatDuration
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentOnePlaylistBinding
import com.example.playlistmaker.getFormattedCount
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.playlists.PlaylistsFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.TRACK_KEY
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.TrackViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class OnePlaylistFragment : Fragment() {

    private var _binding: FragmentOnePlaylistBinding? = null
    private val binding get() = _binding!!

    private val onePlaylistViewModel: OnePlaylistViewModel by viewModel()

    private var playlist: Playlist? = null

    private val trackAdapter: TrackAdapter = TrackAdapter()

    private var isClickAllowed = true

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null


    private val listener = ViewTreeObserver.OnPreDrawListener {
        _binding ?: return@OnPreDrawListener true
        val peekHeight = binding.main.height - binding.onePlaylistFragment.height
        bottomSheetBehavior?.peekHeight = peekHeight
        lifecycleScope.launch {
            delay(1000)
            removeOnPreDrawListener()
        }
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClickAllowed = true

        binding.main.viewTreeObserver.addOnPreDrawListener(listener)

        binding.shareOnePlaylist.setOnClickListener {
            sharePlaylist()
        }

        val bottomSheetBehaviorMenu = BottomSheetBehavior.from(binding.menuBottomSheet)
        bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN

        binding.menuOnePlaylist.setOnClickListener {

            _binding ?: return@setOnClickListener
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HALF_EXPANDED

            bottomSheetBehaviorMenu.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                        }

                        else -> {
                            binding.overlay.isVisible = true
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = (slideOffset + 1f) / 2f
                }
            })
        }


        val playlistId = arguments?.getLong(PlaylistsFragment.PLAYLIST_KEY)
        if (playlistId != null) {
            onePlaylistViewModel.loadPlaylistById(playlistId)
        } else {
            findNavController().popBackStack()
        }


        onePlaylistViewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            this.playlist = playlist
            setupUI(playlist)
        }

        onePlaylistViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.items = tracks.toMutableList()
            if (tracks.isEmpty()) {
                Toast.makeText(requireContext(), "Плейлист пуст", Toast.LENGTH_LONG)
                    .show()
            }
            trackAdapter.notifyDataSetChanged()
        }

        binding.toolbarOnePlaylist.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        onePlaylistViewModel.totalDuration.observe(viewLifecycleOwner) { duration ->
            binding.minutesTv.text = duration
        }

        onePlaylistViewModel.trackCount.observe(viewLifecycleOwner) { count ->
            binding.countTrackTv.text = context?.getFormattedCount(count)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 0f
            }

        })


        binding.bottomSheetRecyclerViewTrack.layoutManager = LinearLayoutManager(requireContext())

        trackAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener { track ->
            intentAudioPlayerFragment(track)
        }

        trackAdapter.onItemLongClickListener = TrackViewHolder.OnItemLongClickListener { track ->
            showDeleteConfirmationDialogTrack(track)
            true
        }

        binding.bottomSheetRecyclerViewTrack.adapter = trackAdapter

        binding.playlistShare.setOnClickListener {
            sharePlaylist()
        }

        binding.playlistDelete.setOnClickListener {
            showDeleteConfirmationDialogPlaylist()
        }

        binding.playlistEditInformation.setOnClickListener {
            playlist?.let {
                navigateToEditPlaylist(it)
            }
        }

    }

    private fun navigateToEditPlaylist(playlist: Playlist) {
        val direction =
            OnePlaylistFragmentDirections.actionOnePlaylistFragmentToNewPlaylistFragment(playlist)
        findNavController().navigate(direction)
    }


    private fun sharePlaylist() {
        playlist?.let { playlist ->
            if (onePlaylistViewModel.tracks.value.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "В этом плейлисте нет списка треков, которым можно поделиться.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val tracksList = onePlaylistViewModel.tracks.value!!.joinToString("\n") { track ->
                    val duration = FormatDuration.formatDuration(track.trackTimeMillis)
                    "${track.artistName} - ${track.trackName} ($duration)"
                }
                val message = """
                ${playlist.name}
                ${playlist.description}
                [${playlist.trackCount}] треков
                $tracksList
            """.trimIndent()

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом с"))
            }
        }
    }


    private fun showDeleteConfirmationDialogPlaylist() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setPositiveButton("ДА") { dialog, _ ->
                playlist?.let {

                    onePlaylistViewModel.removePlaylist(it)
                }
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun showDeleteConfirmationDialogTrack(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек")
            .setMessage("Хотите удалить трек?")
            .setPositiveButton("ДА") { dialog, _ ->
                playlist?.let { onePlaylistViewModel.removeTrack(track.trackId, it.id) }
                dialog.dismiss()
            }
            .setNegativeButton("НЕТ") { dialog, _ ->
            }
            .show()
    }

    private fun setupUI(playlist: Playlist) {
        binding.nameOnePlaylist.text = playlist.name
        binding.descriptionOnePlaylist.text = playlist.description
        val trackCount = context?.getFormattedCount(playlist.trackCount)
        binding.countTrackTv.text = trackCount

        binding.namePlaylistBs.text = playlist.name
        binding.minutesAndTracksBs.text = playlist.trackCount.let { context?.getFormattedCount(it) }

        if (playlist.coverPath?.isNotEmpty() == true) {
            binding.artWorkOnePlaylist.setImageURI(Uri.parse(playlist.coverPath))
            binding.playlistCoverBs.setImageURI(Uri.parse(playlist.coverPath))

        }

    }

    private fun intentAudioPlayerFragment(track: Track) {
        if (clickDebounce()) {
            val bundle = Bundle()
            bundle.putParcelable(TRACK_KEY, track)
            findNavController().navigate(
                R.id.action_onePlaylistFragment_to_audioPlayerFragment2,
                bundle
            )
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

    }

    private fun removeOnPreDrawListener() {
        _binding ?: return
        binding.main.viewTreeObserver.removeOnPreDrawListener(listener)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}