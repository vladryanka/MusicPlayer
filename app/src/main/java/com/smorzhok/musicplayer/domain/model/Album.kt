package com.smorzhok.musicplayer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id: Long,
    val title: String = "",
    val cover: String = ""
): Parcelable