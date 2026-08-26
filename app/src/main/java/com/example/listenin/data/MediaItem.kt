package com.example.listenin.data

import android.net.Uri

enum class MediaType(val route: String) {
    AUDIO("audio"),
    VIDEO("video");

    companion object {
        fun fromRoute(route: String?): MediaType =
            entries.firstOrNull { it.route == route } ?: AUDIO
    }
}

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val title: String,
    val subtitle: String,
    val durationMs: Long,
    val dateAddedSeconds: Long,
    val sizeBytes: Long,
    val type: MediaType
)
