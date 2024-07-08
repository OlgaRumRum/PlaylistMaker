package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale


class AudioProgressUpdater(
    private val mediaPlayer: MediaPlayer,
    private val progressTextView: TextView
) {
    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val formattedTime = dateFormat.format(mediaPlayer.currentPosition)
                progressTextView.text = formattedTime.toString()
                handler.postDelayed(this, TIMER_UPDATE_INTERVAL)
            }
        }
    }

    fun start() {
        if (mediaPlayer.isPlaying) {
            handler.post(progressRunnable)
        }
    }

    fun stop() {
        handler.removeCallbacks(progressRunnable)
    }

    fun reset() {
        progressTextView.text = progressTextView.context.getString(R.string.track_time)

    }

    companion object {
        private const val TIMER_UPDATE_INTERVAL = 300L
    }
}
