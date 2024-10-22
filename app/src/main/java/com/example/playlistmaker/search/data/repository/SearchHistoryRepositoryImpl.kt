package com.example.playlistmaker.search.data.repository


import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun getTrackHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return mutableListOf()

        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }


    override fun addToTrackHistory(newTrack: Track) {
        val tracks = getTrackHistory().toMutableList()
        tracks.removeIf { it.trackId == newTrack.trackId }
        tracks.add(0, newTrack)
        if (tracks.size > MAX_TRACK_HISTORY) {
            tracks.removeLast()
        }
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }


    override fun clearTrackHistory() {
        sharedPreferences.edit {
            remove(HISTORY_KEY)
        }
    }


    companion object {
        private const val MAX_TRACK_HISTORY = 10
        private const val HISTORY_KEY = "history_key"
    }

}
