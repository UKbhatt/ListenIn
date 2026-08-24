package com.example.listenin.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.listenin.ui.theme.AppColors

/**
 * Central place for the app's icons so every screen references the same source.
 */
object AppIcons {
    val Headphones: ImageVector = Icons.Filled.Headphones
    val Video: ImageVector = Icons.Filled.Videocam
    val Audio: ImageVector = Icons.Filled.MusicNote
}

/**
 * The ListenIn brand mark: a headphones icon with no background container.
 * Size and tint are parameterized so the splash (large) and top bar (small)
 * can share one definition.
 */
@Composable
fun HeadphonesLogo(
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
    tint: Color = AppColors.Primary
) {
    Icon(
        imageVector = AppIcons.Headphones,
        contentDescription = "ListenIn",
        tint = tint,
        modifier = modifier.size(size)
    )
}
