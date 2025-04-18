package com.smorzhok.musicplayer.data.mapper

import com.smorzhok.musicplayer.data.dto.AlbumDto
import com.smorzhok.musicplayer.data.dto.TrackDto
import com.smorzhok.musicplayer.domain.model.Album
import com.smorzhok.musicplayer.domain.model.Track

fun TrackDto.toDomain(): Track {
    return Track(
        id = id,
        title = title,
        previewUrl = previewUrl,
        duration = duration,
        artist = artist.name,
        album = album.toDomain(),
        coverUrl = album.cover
    )
}

fun AlbumDto.toDomain(): Album {
    return Album(id, title, cover)
}
