package com.example.playlistmaker.audioPlayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.audioPlayer.data.AddTrackResult
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private var playlistAdapter: BottomSheetAdapter? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        audioPlayerViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter?.submitList(playlists)
        }


        audioPlayerViewModel.loadPlaylists()


        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val track: Track? = arguments?.getParcelable(SearchFragment.TRACK_KEY)

        if (track != null) {
            setupUI(track)
            track.previewUrl?.let { audioPlayerViewModel.preparePlayer(it) }
            audioPlayerViewModel.checkIfTrackIsFavorite(track.trackId)
        } else {
            findNavController().popBackStack()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        audioPlayerViewModel.setSelectedTrack(track)
        playlistAdapter = BottomSheetAdapter { playlist ->
            audioPlayerViewModel.selectedTrack.value?.let { track ->
                audioPlayerViewModel.addTrackToPlaylist(playlist, track)

            } ?: run {
                Toast.makeText(requireContext(), "Ошибка: Трек не найден!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.bottomSheetRecyclerView.adapter = playlistAdapter


        audioPlayerViewModel.addTrackResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddTrackResult.Success -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(
                        context,
                        "Трек успешно добавлен в плейлист ${result.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AddTrackResult.DuplicateTrack -> {
                    Toast.makeText(
                        context,
                        "Трек уже добавлен в плейлист ${result.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }



        binding.buttonFavorite.setOnClickListener {

            track?.let { currentTrack ->
                audioPlayerViewModel.onFavoriteClicked(currentTrack)
            }
        }


        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

        audioPlayerViewModel.trackInfo.observe(viewLifecycleOwner, Observer { trackInfo ->
            binding.progressTime.text = trackInfo?.currentPosition
        })

        audioPlayerViewModel.isFavorite.observe(viewLifecycleOwner, Observer { isFavorite ->
            updateLikeButton(isFavorite)
        })


        audioPlayerViewModel.playbackState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                AudioPlayerViewModel.STATE_PLAYING -> binding.buttonPlay.setImageResource(R.drawable.button_pause)
                AudioPlayerViewModel.STATE_PAUSED, AudioPlayerViewModel.STATE_PREPARED -> binding.buttonPlay.setImageResource(
                    R.drawable.button_play
                )

                AudioPlayerViewModel.STATE_COMPLETED -> {
                    binding.buttonPlay.setImageResource(R.drawable.button_play)
                }
            }
        })



        binding.buttonAdd.setOnClickListener {
            audioPlayerViewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
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
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        binding.bottomSheetAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment2_to_newPlaylistFragment)
        }


    }


    private fun updateLikeButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.buttonFavorite.setImageResource(R.drawable.button_favorite)
        } else {
            binding.buttonFavorite.setImageResource(R.drawable.button_not_favorite)
        }
    }


    override fun onStop() {
        super.onStop()
        audioPlayerViewModel.pausePlayer()
    }

    private fun setupUI(track: Track) {
        binding.trackNameAudioPlayer.text = track.trackName
        binding.artist.text = track.artistName
        binding.trackTime.text = dateFormat.format(track.trackTimeMillis)
        binding.albumName.text = track.collectionName.ifEmpty { "" }
        binding.yearName.text = audioPlayerViewModel.formatReleaseDate(track.releaseDate)
        binding.genreName.text = track.primaryGenreName
        binding.countryName.text = track.country

        Glide.with(this)

            .load(audioPlayerViewModel.formatArtworkUrl(track.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.circleRadius_artwork)))
            .into(binding.artWorkAudioPlayer)

    }

    private fun playbackControl() {
        when (audioPlayerViewModel.playbackState.value) {
            AudioPlayerViewModel.STATE_PLAYING -> audioPlayerViewModel.pausePlayer()
            AudioPlayerViewModel.STATE_PREPARED, AudioPlayerViewModel.STATE_PAUSED -> audioPlayerViewModel.startPlayer()
            AudioPlayerViewModel.STATE_COMPLETED ->
                audioPlayerViewModel.startPlayer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}