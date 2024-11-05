package com.example.playlistmaker.media.ui.playlist

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist
import java.io.File

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val playlistTrackCount: TextView = itemView.findViewById(R.id.playlist_trackCount)


    fun bind(playlist: Playlist) {
        if (playlist.coverPath?.isNotEmpty() == true) {
            playlistCover.scaleType = ImageView.ScaleType.CENTER_CROP
            val filePath =
                File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")
            val file = File(filePath, playlist.coverPath)

            Glide.with(itemView)
                .load(file.toUri())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.playlistCover_radius)))
                .into(playlistCover)
        }
        playlistName.text = playlist.name
        val trackCount = playlist.trackIds.size
        playlistTrackCount.text =
            itemView.context.getString(R.string.playlist_track_count, trackCount)

    }
}