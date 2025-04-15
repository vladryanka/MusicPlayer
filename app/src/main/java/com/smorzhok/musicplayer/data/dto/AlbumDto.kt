package com.smorzhok.musicplayer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: Long,
    val title: String,
    @SerialName("cover_medium") val cover: String
)