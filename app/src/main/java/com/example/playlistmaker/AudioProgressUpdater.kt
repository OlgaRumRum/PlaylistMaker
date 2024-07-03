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
    private val updateInterval = 300L

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val formattedTime = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                progressTextView.text = formattedTime.toString()
                handler.postDelayed(this, updateInterval)
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
        progressTextView.text = "00:00"

    }
}
