package com.smorzhok.musicplayer.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackListDto(
    val data: List<TrackDto> = emptyList()
)
