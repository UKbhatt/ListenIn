package com.example.listenin.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

object ScreenPadding {
    val horizontal: Dp
        @Composable
        get() = when {
            LocalConfiguration.current.screenWidthDp <= 360 -> 16.dp
            LocalConfiguration.current.screenWidthDp <= 480 -> 20.dp
            else -> 24.dp
        }

    val top: Dp
        @Composable
        get() = when {
            LocalConfiguration.current.screenHeightDp <= 640 -> 12.dp
            else -> 18.dp
        }

    val bottom: Dp
        @Composable
        get() = when {
            LocalConfiguration.current.screenHeightDp <= 640 -> 16.dp
            else -> 24.dp
        }

    val values: PaddingValues
        @Composable
        get() = PaddingValues(
            start = horizontal,
            top = top,
            end = horizontal,
            bottom = bottom
        )
}
