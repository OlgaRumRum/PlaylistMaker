package com.example.playlistmaker.media.ui.playlist

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.getFormattedCount
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val playlistTrackCount: TextView = itemView.findViewById(R.id.playlist_trackCount)


    fun bind(playlist: Playlist) {
        if (playlist.coverPath?.isNotEmpty() == true) {
            playlistCover.setImageURI(Uri.parse(playlist.coverPath))

        }
        playlistName.text = playlist.name
        val trackCount = itemView.context.getFormattedCount(playlist.trackCount)
        playlistTrackCount.text = trackCount

    }

}