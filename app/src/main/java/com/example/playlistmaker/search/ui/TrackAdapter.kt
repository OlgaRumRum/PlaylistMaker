package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.search.domain.models.Track


class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {
    var onItemClickListener: TrackViewHolder.OnItemClickListener? = null
    var onItemLongClickListener: TrackViewHolder.OnItemLongClickListener? = null
    var items: MutableList<Track> = mutableListOf()

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(
            item = items[position],
            onItemClickListener = onItemClickListener,
            onItemLongClickListener = onItemLongClickListener
        )

    }

    override fun getItemCount(): Int = items.size
}