package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val TRACKS_HISTORY_KEY = "track_history_key"
const val MAX_TRACK_HISTORY = 10

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    fun getTrackHistory(): List<Track> {
        val json = sharedPrefs.getString(TRACKS_HISTORY_KEY, null) ?: return listOf()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun addToTrackHistory(newTrack: Track) {
        val tracks = getTrackHistory().toMutableList()

        val existingTrack = tracks.find { it.trackId == newTrack.trackId }
        existingTrack?.let {
            tracks.remove(it)
        }
        tracks.add(0, newTrack)

        if (tracks.size > MAX_TRACK_HISTORY) {
            tracks.removeAt(tracks.size - 1)
        }
        saveTrackHistory(tracks)

    }

    fun clearTrackHistory() {
        sharedPrefs.edit()
            .remove(TRACKS_HISTORY_KEY)
            .apply()
    }

    fun saveTrackHistory(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(TRACKS_HISTORY_KEY, json)
            .apply()
    }


}


