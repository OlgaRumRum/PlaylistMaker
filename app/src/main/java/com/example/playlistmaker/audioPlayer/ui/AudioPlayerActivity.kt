package com.example.playlistmaker.audioPlayer.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding


    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }


    private val audioPlayerViewModel by viewModel<AudioPlayerViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }


        val track: Track? = intent.getParcelableExtra(SearchFragment.TRACK_KEY)

        if (track != null) {
            setupUI(track)
            track.previewUrl?.let { audioPlayerViewModel.preparePlayer(it) }
            audioPlayerViewModel.checkIfTrackIsFavorite(track.trackId)
        } else {
            finish()
        }


        binding.buttonFavorite.setOnClickListener {

            track?.let { currentTrack ->
                audioPlayerViewModel.onFavoriteClicked(currentTrack)
                setResult(Activity.RESULT_OK)
            }
        }


        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

        audioPlayerViewModel.trackInfo.observe(this, Observer { trackInfo ->
            binding.progressTime.text = trackInfo?.currentPosition
        })

        audioPlayerViewModel.isFavorite.observe(this, Observer { isFavorite ->
            updateLikeButton(isFavorite)
        })


        audioPlayerViewModel.playbackState.observe(this, Observer { state ->
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
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.circleRadius_artwork)))
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

}




