package com.example.playlistmaker

import android.content.Context


fun Context.getFormattedCount(trackCount: Int): String {
    val n = trackCount % 100

    return when {
        n in 11..14 -> String.format(getString(R.string.track_a), trackCount)
        n % 10 == 1 -> String.format(getString(R.string.track), trackCount)
        n % 10 in 2..4 -> String.format(getString(R.string.tracks), trackCount)
        else -> String.format(getString(R.string.track_a), trackCount)
    }
}
