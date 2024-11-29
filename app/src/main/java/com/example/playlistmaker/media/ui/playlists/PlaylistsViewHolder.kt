package com.example.playlistmaker.media.ui.playlists

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.getFormattedCount
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistsViewHolder(val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, onItemClickListener: OnItemClickListener?) {
        if (playlist.coverPath?.isNotEmpty() == true) {
            binding.playlistCover.setImageURI(Uri.parse(playlist.coverPath))

        }
        binding.playlistName.text = playlist.name
        val trackCount = itemView.context.getFormattedCount(playlist.trackCount)
        binding.playlistTrackCount.text = trackCount

        binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(playlist)
        }

    }

    fun interface OnItemClickListener {
        fun onItemClick(item: Playlist)
    }

}
