package com.example.playlistmaker.presentation.ui.audioPlayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var buttonPlay: ImageButton
    private lateinit var progressTextView: TextView
    private lateinit var progressUpdater: AudioProgressUpdater
    private var mediaPlayer = MediaPlayer()

    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val artworkUrl = findViewById<ImageView>(R.id.artWork_audio_player)
        val trackName = findViewById<TextView>(R.id.trackName_audio_player)
        val artist = findViewById<TextView>(R.id.artist)

        val duration = findViewById<TextView>(R.id.trackTime)
        val album = findViewById<TextView>(R.id.albumName)
        val year = findViewById<TextView>(R.id.yearName)
        val genre = findViewById<TextView>(R.id.genreName)
        val country = findViewById<TextView>(R.id.countryName)

        buttonPlay = findViewById(R.id.button_play)
        progressTextView = findViewById(R.id.progressTime)
        progressUpdater = AudioProgressUpdater(mediaPlayer, progressTextView)


        val backArrowButton = findViewById<ImageButton>(R.id.back_arrow_button)
        backArrowButton.setOnClickListener { finish() }

        val track: Track? = intent.getParcelableExtra(SearchActivity.TRACK_KEY)


        if (track != null) {
            preparePlayer(track)
        } else {
            finish()
        }

        buttonPlay.setOnClickListener {
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
            buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            buttonPlay.setImageResource(R.drawable.button_play)
            progressUpdater.reset()

        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
        progressUpdater.start()

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        buttonPlay.setImageResource(R.drawable.button_play)
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

