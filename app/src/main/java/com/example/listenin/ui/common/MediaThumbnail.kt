package com.example.listenin.ui.common

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.listenin.data.MediaItem
import com.example.listenin.data.MediaType
import com.example.listenin.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MediaThumbnail(
    item: MediaItem,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember(item.uri) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(item.uri) {
        bitmap = loadThumbnail(context, item.uri)
    }

    Box(
        modifier = modifier.background(AppColors.Card),
        contentAlignment = Alignment.Center
    ) {
        val loaded = bitmap
        if (loaded != null) {
            Image(
                bitmap = loaded.asImageBitmap(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = placeholderIcon(item.type),
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun placeholderIcon(type: MediaType): ImageVector =
    if (type == MediaType.VIDEO) AppIcons.Video else AppIcons.Audio

private suspend fun loadThumbnail(
    context: android.content.Context,
    uri: Uri
): Bitmap? = withContext(Dispatchers.IO) {
    // loadThumbnail (content-resolver level) is only available on API 29+.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return@withContext null
    runCatching {
        context.contentResolver.loadThumbnail(uri, Size(256, 256), null)
    }.getOrNull()
}
