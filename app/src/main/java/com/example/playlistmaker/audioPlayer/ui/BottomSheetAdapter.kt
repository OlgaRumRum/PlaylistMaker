package com.example.playlistmaker.audioPlayer.ui

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.getFormattedCount
import com.example.playlistmaker.media.domain.models.Playlist
import java.io.File


class BottomSheetAdapter(private val onPlaylistClick: (Playlist) -> Unit) :
    ListAdapter<Playlist, BottomSheetAdapter.BottomSheetViewHolder>(PlaylistDiffCallback()) {

    class BottomSheetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val playlistCover: ImageView = itemView.findViewById(R.id.cover_iv)
        private val playlistName: TextView = itemView.findViewById(R.id.title_tv)
        private val playlistTrackCount: TextView = itemView.findViewById(R.id.count_tv)

        fun bind(playlist: Playlist) {
            playlist.coverPath?.let { path ->
                if (path.isNotEmpty()) {
                    playlistCover.scaleType = ImageView.ScaleType.CENTER_CROP
                    val filePath = File(
                        itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "cache"
                    )
                    val file = File(filePath, path)

                    Glide.with(itemView)
                        .load(playlist.coverPath)
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.playlistCover_radius)))
                        .into(playlistCover)
                }
            } ?: run {
                playlistCover.setImageResource(R.drawable.placeholder)
            }

            playlistName.text = playlist.name
            playlistTrackCount.text = itemView.context.getFormattedCount(playlist.trackCount)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return BottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onPlaylistClick(getItem(position))
        }
    }


    class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem == newItem
    }
}
