package com.smorzhok.musicplayer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WrappedAlbumData(
    @SerialName("data") val data: List<AlbumDto>
)