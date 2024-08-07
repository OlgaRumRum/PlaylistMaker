package com.example.playlistmaker.audioPlayer.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchActivity
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding


    private val audioPlayerInteractor = Creator.provideAudioPlayerInteractor()

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }


    private val viewModel by lazy {
        ViewModelProvider(
            this,
            AudioPlayerViewModel.getViewModelFactory(audioPlayerInteractor)
        )[AudioPlayerViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnClickListener {
            finish()
        }


        val track: Track? = intent.getParcelableExtra(SearchActivity.TRACK_KEY)

        if (track != null) {
            setupUI(track)
            track.previewUrl?.let { viewModel.preparePlayer(it) }
        } else {
            showErrorDialog()
            finish()
        }

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

        viewModel.trackInfo.observe(this, Observer { trackInfo ->
            binding.progressTime.text = trackInfo?.currentPosition
        })

        viewModel.playbackState.observe(this, Observer { state ->
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

    private fun setupUI(track: Track) {
        binding.trackNameAudioPlayer.text = track.trackName
        binding.artist.text = track.artistName
        binding.trackTime.text = dateFormat.format(track.trackTimeMillis)
        binding.albumName.text = track.collectionName.ifEmpty { "" }
        binding.yearName.text = track.releaseDate?.substring(0, 4) ?: "Unknown"
        binding.genreName.text = track.primaryGenreName
        binding.countryName.text = track.country

        Glide.with(this)
            .load(track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.circleRadius_artwork)))
            .into(binding.artWorkAudioPlayer)
    }


    private fun showErrorDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(R.string.track_not_found_error)
            .setPositiveButton(R.string.Button_Ok) { dialog, which ->
                finish()
            }
            .create()
        dialog.show()
    }

    private fun playbackControl() {
        when (viewModel.playbackState.value) {
            AudioPlayerViewModel.STATE_PLAYING -> viewModel.pausePlayer()
            AudioPlayerViewModel.STATE_PREPARED, AudioPlayerViewModel.STATE_PAUSED -> viewModel.startPlayer()
            AudioPlayerViewModel.STATE_COMPLETED ->
                viewModel.startPlayer()
        }
    }


}

