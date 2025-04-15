package com.smorzhok.musicplayer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DownloadedTrackEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun downloadedTrackDao(): DownloadedTrackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}