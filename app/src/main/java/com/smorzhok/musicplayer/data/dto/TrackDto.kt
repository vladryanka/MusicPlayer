package com.smorzhok.musicplayer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    val id: Long,
    val title: String,
    @SerialName("preview") val previewUrl: String,
    val duration: Int,
    val artist: ArtistDto,
    val album: AlbumDto
)
