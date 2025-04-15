package com.smorzhok.musicplayer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: Long,
    val name: String,
    @SerialName("picture_medium") val picture: String
)