package com.example.listenin.ui.medialist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.listenin.data.MediaItem
import com.example.listenin.data.MediaRepository
import com.example.listenin.data.MediaType
import com.example.listenin.ui.common.GenericScaffold
import com.example.listenin.ui.common.MediaFormat
import com.example.listenin.ui.common.MediaThumbnail
import com.example.listenin.ui.theme.AppColors

private sealed interface MediaListState {
    data object Loading : MediaListState
    data object PermissionRequired : MediaListState
    data object Empty : MediaListState
    data class Content(val items: List<MediaItem>) : MediaListState
}

private fun requiredPermission(type: MediaType): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        when (type) {
            MediaType.AUDIO -> Manifest.permission.READ_MEDIA_AUDIO
            MediaType.VIDEO -> Manifest.permission.READ_MEDIA_VIDEO
        }
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

@Composable
fun MediaListScreen(
    type: MediaType,
    onBack: () -> Unit = {},
    onItemClick: (MediaItem) -> Unit = {}
) {
    val context = LocalContext.current
    val permission = remember(type) { requiredPermission(type) }

    var hasPermission by remember(permission) {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    val state by produceState<MediaListState>(
        initialValue = if (hasPermission) MediaListState.Loading else MediaListState.PermissionRequired,
        key1 = hasPermission,
        key2 = type
    ) {
        value = if (!hasPermission) {
            MediaListState.PermissionRequired
        } else {
            val items = MediaRepository.loadMedia(context, type)
            if (items.isEmpty()) MediaListState.Empty else MediaListState.Content(items)
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            AppColors.Background,
            AppColors.BackgroundSecondary,
            AppColors.BackgroundAccent
        )
    )

    GenericScaffold(
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Header(type = type, onBack = onBack)

            when (val current = state) {
                MediaListState.Loading -> CenteredMessage { CircularProgressIndicator(color = AppColors.Primary) }
                MediaListState.PermissionRequired -> PermissionPrompt(type = type) {
                    permissionLauncher.launch(permission)
                }
                MediaListState.Empty -> CenteredMessage {
                    Text(
                        text = "No ${type.route} files found on this device",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextSecondary
                    )
                }
                is MediaListState.Content -> MediaList(items = current.items, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
private fun Header(type: MediaType, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AppColors.TextPrimary
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (type == MediaType.VIDEO) "Videos" else "Audio",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = AppColors.TextPrimary
        )
    }
}

@Composable
private fun MediaList(items: List<MediaItem>, onItemClick: (MediaItem) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            MediaRow(item = item, onClick = { onItemClick(item) })
        }
    }
}

@Composable
private fun MediaRow(item: MediaItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Thumbnail(item = item)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = AppColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = metaLine(item),
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Thumbnail(item: MediaItem) {
    val isVideo = item.type == MediaType.VIDEO
    val thumbModifier = if (isVideo) {
        Modifier.width(104.dp).height(60.dp)
    } else {
        Modifier.size(60.dp)
    }
    Box {
        MediaThumbnail(
            item = item,
            modifier = thumbModifier.clip(RoundedCornerShape(10.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text(
                text = MediaFormat.duration(item.durationMs),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

private fun metaLine(item: MediaItem): String {
    val size = MediaFormat.size(item.sizeBytes)
    val date = MediaFormat.date(item.dateAddedSeconds)
    return if (size.isBlank()) date else "$date  •  $size"
}

@Composable
private fun PermissionPrompt(type: MediaType, onGrant: () -> Unit) {
    CenteredMessage {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Allow access to your ${type.route} files to see them here",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onGrant,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Text(text = "Grant permission", color = AppColors.TextPrimary)
            }
        }
    }
}

@Composable
private fun CenteredMessage(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
