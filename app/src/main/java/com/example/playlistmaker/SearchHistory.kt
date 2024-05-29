package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

class SearchHistory(
    private val sharedPrefs: SharedPreferences, private val adapter: TrackAdapter
) {


    init {
        updateAdapter(sharedPrefs)
        listener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

                if (key == HISTORY_KEY) {
                    updateAdapter(sharedPreferences)
                }
            }

        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }


    private fun updateAdapter(sharedPreferences: SharedPreferences?) {
        val jsonTracks = sharedPreferences?.getString(HISTORY_KEY, null)
        if (jsonTracks != null) {
            val tracks = getTrackHistory()
            adapter.items.clear()
            adapter.items.addAll(tracks)
            adapter.notifyDataSetChanged()
        }
    }


    fun getTrackHistory(): List<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, null) ?: return listOf()
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
        sharedPrefs.edit()
            .remove(HISTORY_KEY)
            .apply()

    }

    private fun saveTrackHistory(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    companion object {
        const val MAX_TRACK_HISTORY = 10
        const val HISTORY_KEY = "history_key"
    }


}


