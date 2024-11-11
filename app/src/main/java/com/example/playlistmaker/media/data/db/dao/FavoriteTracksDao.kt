package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.TracksEntity


@Dao
interface FavoriteTracksDao {
    @Insert(entity = TracksEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(track: List<TracksEntity>)

    @Delete(entity = TracksEntity::class)
    suspend fun deleteTracks(track: TracksEntity)

    @Query("SELECT * FROM favorites_tracks_table ORDER BY addedAt DESC")
    suspend fun getFavoriteList(): List<TracksEntity>

    @Query("SELECT trackId FROM favorites_tracks_table")
    suspend fun getFavoriteIdList(): List<Long>

    @Query("SELECT COUNT(*) FROM favorites_tracks_table WHERE trackId = :trackId")
    suspend fun isFavorite(trackId: Long): Long

}