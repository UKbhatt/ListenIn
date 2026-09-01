package com.example.listenin.playback

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Bridge the player screen uses to talk to the host Activity about Picture-in-Picture:
 * report whether a video is currently eligible for PiP (and its aspect ratio), read
 * whether we are currently in PiP so chrome can be hidden, and enter PiP on demand.
 */
interface PipController {
    val isInPipMode: Boolean
    fun onVideoState(active: Boolean, width: Int, height: Int)
    fun enterPip()
}

val LocalPipController = staticCompositionLocalOf<PipController?> { null }
