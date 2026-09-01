package com.example.listenin

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.listenin.playback.LocalPipController
import com.example.listenin.playback.PipController
import com.example.listenin.ui.navigation.AppNavHost
import com.example.listenin.ui.theme.ListenInTheme

class MainActivity : ComponentActivity(), PipController {

    private var isInPip by mutableStateOf(false)
    private var videoActive = false
    private var videoAspect = DEFAULT_ASPECT

    override val isInPipMode: Boolean get() = isInPip

    override fun onVideoState(active: Boolean, width: Int, height: Int) {
        videoActive = active
        if (active && width > 0 && height > 0) videoAspect = clampAspect(width, height)
        updatePipParams()
    }

    override fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(videoAspect).build()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPipParams(): PictureInPictureParams {
        val builder = PictureInPictureParams.Builder().setAspectRatio(videoAspect)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) builder.setAutoEnterEnabled(videoActive)
        return builder.build()
    }

    private fun updatePipParams() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        setPictureInPictureParams(buildPipParams())
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Auto-enter (setAutoEnterEnabled) only exists on API 31+; enter manually below it.
        val needsManualEntry = Build.VERSION.SDK_INT in Build.VERSION_CODES.O until Build.VERSION_CODES.S
        if (videoActive && needsManualEntry) enterPip()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPip = isInPictureInPictureMode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListenInTheme {
                CompositionLocalProvider(LocalPipController provides this) {
                    AppNavHost()
                }
            }
        }
    }

    private companion object {
        val DEFAULT_ASPECT = Rational(16, 9)

        // PiP rejects aspect ratios outside roughly [1/2.39, 2.39]; clamp to stay in range.
        const val MAX_RATIO = 2.39
        const val MIN_RATIO = 1.0 / 2.39
        const val RATIO_SCALE = 1000

        fun clampAspect(width: Int, height: Int): Rational {
            val ratio = (width.toDouble() / height.toDouble()).coerceIn(MIN_RATIO, MAX_RATIO)
            return Rational((ratio * RATIO_SCALE).toInt(), RATIO_SCALE)
        }
    }
}
