package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.ApplicationContextProvider
import com.example.playlistmaker.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.TrackAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

class SearchHistoryRepositoryImpl(
    private val applicationContextProvider: ApplicationContextProvider,
    private val adapter: TrackAdapter
) : SearchHistoryRepository {

    private val searchHistoryPrefs: SharedPreferences by lazy {
        applicationContextProvider.applicationContext.getSharedPreferences(
            HISTORY_KEY,
            Context.MODE_PRIVATE
        )
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

    override fun getTrackHistory(): List<Track> {
        val json = searchHistoryPrefs.getString(HISTORY_KEY, null) ?: return listOf()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun addToTrackHistory(track: Track) {
        val tracks = getTrackHistory().toMutableList()
        if (tracks.contains(track)) {
            tracks.remove(track)
        }
        tracks.add(0, track)
        if (tracks.size > MAX_TRACK_HISTORY) {
            tracks.removeLast()
        }
        saveTrackHistory(tracks)
    }

    override fun clearTrackHistory() {
        searchHistoryPrefs.edit()
            .remove(HISTORY_KEY)
            .apply()
    }

    override fun saveTrackHistory(tracks: List<Track>) {
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

