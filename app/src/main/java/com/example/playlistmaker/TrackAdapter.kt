package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var items = ArrayList<Track>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = items[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(track)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

fun interface ItemClickListener {
    fun onItemClick(track: Track)
}