package com.smorzhok.musicplayer.domain.model

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Int,
    val previewUrl: String,
    val coverUrl: String
)
