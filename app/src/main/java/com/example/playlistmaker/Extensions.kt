package com.example.playlistmaker

import android.content.Context


fun Context.getFormattedCount(trackCount: Int): String {
    val n = trackCount % 100

    return when {
        n in 11..14 -> String.format(getString(R.string.tracks), trackCount)
        n % 10 == 1 -> String.format(getString(R.string.track), trackCount)
        n % 10 in 2..4 -> String.format(getString(R.string.track_a), trackCount)
        else -> String.format(getString(R.string.tracks), trackCount)
    }
}


fun Context.durationTextFormater(duration: Int): String {
    val lastDigit = duration % 10
    val lastTwoDigits = duration % 100

    return when {
        lastTwoDigits in 11..14 -> String.format(getString(R.string.minutes), duration)
        lastDigit == 1 -> String.format(getString(R.string.minute), duration)
        lastDigit in 2..4 -> String.format(getString(R.string.minutes_s), duration)
        else -> String.format(getString(R.string.minutes), duration)
    }
}
