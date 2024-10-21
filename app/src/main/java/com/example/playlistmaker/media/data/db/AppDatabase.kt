package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavoriteTracksDao
import com.example.playlistmaker.media.data.db.entity.TracksEntity

@Database(version = 1, entities = [TracksEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTracksDao(): FavoriteTracksDao

}