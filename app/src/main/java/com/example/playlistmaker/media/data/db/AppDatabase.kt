package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavoriteTracksDao
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.TracksEntity

@Database(
    entities = [TracksEntity::class, PlaylistEntity::class],
    version = 1
)
//exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao

}