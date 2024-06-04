package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

class SearchHistory(
    private val context: Context,
    private val adapter: TrackAdapter
) {

    private val searchHistoryPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(HISTORY_KEY, Context.MODE_PRIVATE)
    }

    init {
        updateAdapter(searchHistoryPrefs)
        listener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

                if (key == HISTORY_KEY) {
                    updateAdapter(sharedPreferences)
                }
            }

        searchHistoryPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun updateAdapter(sharedPreferences: SharedPreferences?) {
        val jsonTracks = sharedPreferences?.getString(HISTORY_KEY, null)
        jsonTracks?.let {
            val tracks = getTrackHistory()
            adapter.items.clear()
            adapter.items.addAll(tracks)
            adapter.notifyDataSetChanged()
        }
    }


    fun getTrackHistory(): List<Track> {
        val json = searchHistoryPrefs.getString(HISTORY_KEY, null) ?: return listOf()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun addToTrackHistory(newTrack: Track) {
        val tracks = getTrackHistory().toMutableList()
        if (tracks.contains(newTrack)) {
            tracks.remove(newTrack)
        }
        tracks.add(0, newTrack)
        if (tracks.size > MAX_TRACK_HISTORY) {
            tracks.removeLast()
        }
        saveTrackHistory(tracks)

    }

    fun clearTrackHistory() {
        searchHistoryPrefs.edit()
            .remove(HISTORY_KEY)
            .apply()
    }

    private fun saveTrackHistory(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        searchHistoryPrefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    companion object {
        private const val MAX_TRACK_HISTORY = 10
        private const val HISTORY_KEY = "history_key"
    }


}


