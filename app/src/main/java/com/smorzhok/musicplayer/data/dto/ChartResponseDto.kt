package com.smorzhok.musicplayer.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChartResponseDto(
    val tracks: TrackListDto
)