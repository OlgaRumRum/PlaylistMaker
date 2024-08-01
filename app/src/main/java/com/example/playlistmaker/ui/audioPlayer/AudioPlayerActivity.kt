package com.example.playlistmaker.ui.audioPlayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var progressUpdater: AudioProgressUpdater
    private var mediaPlayer = MediaPlayer()

    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val artworkUrl = binding.artWorkAudioPlayer
        val trackName = binding.trackNameAudioPlayer
        val artist = binding.artist

        val duration = binding.trackTime
        val album = binding.albumName
        val year = binding.yearName
        val genre = binding.genreName
        val country = binding.countryName

        progressUpdater = AudioProgressUpdater(mediaPlayer, binding.progressTime)

        binding.backArrowButton.setOnClickListener { finish() }

        val track: Track? = intent.getParcelableExtra(SearchActivity.TRACK_KEY)


        if (track != null) {
            preparePlayer(track)
        } else {
            finish()
        }

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }


        if (track != null) {
            trackName.text = track.trackName
            artist.text = track.artistName
            duration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            album.text = track.collectionName.ifEmpty { "" }
            year.text = track.releaseDate.substring(0, 4)
            genre.text = track.primaryGenreName
            country.text = track.country
        } else {
            showErrorDialog()
        }

        Glide.with(this)
            .load(track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.circleRadius_artwork)))
            .into(artworkUrl)

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

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.buttonPlay.setImageResource(R.drawable.button_play)
            progressUpdater.reset()

        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.buttonPlay.setImageResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
        progressUpdater.start()

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.buttonPlay.setImageResource(R.drawable.button_play)
        playerState = STATE_PAUSED
        progressUpdater.stop()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        progressUpdater.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        progressUpdater.stop()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}

