package com.example.playlistmaker.media.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.media.ui.favorite.FavoriteTracksFragment
import com.example.playlistmaker.media.ui.playlist.PlaylistFragment

class MediaViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> PlaylistFragment.newInstance()
            else -> FavoriteTracksFragment.newInstance()
        }
    }
}