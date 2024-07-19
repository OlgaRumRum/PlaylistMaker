package com.example.playlistmaker.presentation.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val songName: TextView = itemView.findViewById(R.id.trackName)
    private val songArtist: TextView = itemView.findViewById(R.id.artistName)
    private val songTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artwork: ImageView = itemView.findViewById(R.id.artWork)

    fun bind(item: Track) {

        songName.text = item.trackName
        songArtist.text = item.artistName
        songTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)


        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.artWorkUrl100_radius)))
            .into(artwork)


    }
}
