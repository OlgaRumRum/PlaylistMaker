package com.example.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Track, onItemClickListener: OnItemClickListener?) {
        binding.apply {
            trackName.text = item.trackName
            artistName.text = item.artistName
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)

            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.artWorkUrl100_radius)))
                .into(artWork)

            root.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    fun interface OnItemClickListener {
        fun onItemClick(item: Track)
    }
}

