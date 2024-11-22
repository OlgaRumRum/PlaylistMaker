package com.example.playlistmaker.media.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.media.domain.models.Playlist


class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsViewHolder>() {

    var onItemClickListener: PlaylistsViewHolder.OnItemClickListener? = null

    var playlists: MutableList<Playlist> = mutableListOf()

    fun clear() {
        playlists.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val binding =
            PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position], onItemClickListener = onItemClickListener)
    }
}
