package com.smorzhok.musicplayer.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DownloadedTrackDao {

    @Query("SELECT * FROM downloaded_tracks")
    suspend fun getAllTracks(): List<DownloadedTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: DownloadedTrackEntity)

    @Delete
    suspend fun deleteTrack(track: DownloadedTrackEntity)

    @Query("SELECT * FROM downloaded_tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    suspend fun searchTracks(query: String): List<DownloadedTrackEntity>
}