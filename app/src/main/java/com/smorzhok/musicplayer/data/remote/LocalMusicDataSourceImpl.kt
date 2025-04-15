package com.smorzhok.musicplayer.data.remote

import android.content.Context
import android.provider.MediaStore
import com.smorzhok.musicplayer.data.dto.AlbumDto
import com.smorzhok.musicplayer.data.dto.ArtistDto
import com.smorzhok.musicplayer.data.dto.TrackDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalMusicDataSourceImpl(private val context: Context) : LocalMusicDataSource {

    override suspend fun getAllTracks(): List<TrackDto> = withContext(Dispatchers.IO) {
        getTracksFromMediaStore()
    }

    private fun getTracksFromMediaStore(): List<TrackDto> {
        val trackList = mutableListOf<TrackDto>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val path = cursor.getString(dataColumn)
                val duration = cursor.getInt(durationColumn)
                val album = cursor.getString(albumColumn)

                val track = TrackDto(
                    id = id,
                    title = title,
                    artist = ArtistDto(
                        id = 0,
                        name = artist,
                        picture = ""
                    ),
                    album = AlbumDto(
                        id = 0,
                        title = album,
                        cover = ""
                    ),
                    previewUrl = path,
                    duration = duration
                )
                trackList.add(track)
            }
        }
        return trackList
    }
}