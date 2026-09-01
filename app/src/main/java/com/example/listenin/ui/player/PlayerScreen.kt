package com.example.listenin.ui.player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.session.MediaController
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.listenin.data.MediaItem
import com.example.listenin.data.MediaRepository
import com.example.listenin.data.MediaType
import com.example.listenin.playback.LocalPipController
import com.example.listenin.playback.rememberMediaController
import com.example.listenin.ui.common.GenericScaffold
import com.example.listenin.ui.common.MediaFormat
import com.example.listenin.ui.common.MediaThumbnail
import com.example.listenin.ui.theme.AppColors
import kotlinx.coroutines.delay

private sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data object NotFound : PlayerUiState
    data class Ready(val item: MediaItem) : PlayerUiState
}

@Composable
fun PlayerScreen(type: MediaType, id: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    val controller = rememberMediaController()

    RequestNotificationPermission()

    val state by produceState<PlayerUiState>(PlayerUiState.Loading, type, id) {
        val item = MediaRepository.loadItem(context, type, id)
        value = if (item == null) PlayerUiState.NotFound else PlayerUiState.Ready(item)
    }

    when (val current = state) {
        PlayerUiState.Loading -> PlayerScaffold(onBack = onBack) {
            CenteredMessage { CircularProgressIndicator(color = AppColors.Primary) }
        }
        PlayerUiState.NotFound -> PlayerScaffold(onBack = onBack) {
            CenteredMessage {
                Text(
                    text = "This item is no longer available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextSecondary
                )
            }
        }
        is PlayerUiState.Ready -> {
            val ready = controller
            if (ready == null) {
                PlayerScaffold(onBack = onBack) {
                    CenteredMessage { CircularProgressIndicator(color = AppColors.Primary) }
                }
            } else {
                PlayerContent(item = current.item, controller = ready, onBack = onBack)
            }
        }
    }
}

@Composable
private fun PlayerContent(item: MediaItem, controller: MediaController, onBack: () -> Unit) {
    val pip = LocalPipController.current
    val isVideo = item.type == MediaType.VIDEO

    LaunchedEffect(controller, item.id) {
        if (controller.currentMediaItem?.mediaId != item.id.toString()) {
            controller.setMediaItem(
                androidx.media3.common.MediaItem.Builder()
                    .setUri(item.uri)
                    .setMediaId(item.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(item.title)
                            .setArtist(item.subtitle)
                            .build()
                    )
                    .build()
            )
            controller.prepare()
            controller.playWhenReady = true
        }
    }

    var isPlaying by remember { mutableStateOf(controller.isPlaying) }
    var position by remember { mutableLongStateOf(controller.currentPosition) }
    var duration by remember { mutableLongStateOf(item.durationMs) }
    var videoSize by remember { mutableStateOf(controller.videoSize) }

    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
            override fun onPlaybackStateChanged(state: Int) {
                if (controller.duration > 0) duration = controller.duration
            }
            override fun onVideoSizeChanged(size: VideoSize) { videoSize = size }
        }
        controller.addListener(listener)
        onDispose { controller.removeListener(listener) }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            position = controller.currentPosition
            delay(500)
        }
    }

    LaunchedEffect(isVideo, isPlaying, videoSize) {
        pip?.onVideoState(isVideo && isPlaying, videoSize.width, videoSize.height)
    }
    DisposableEffect(Unit) {
        onDispose { pip?.onVideoState(false, 0, 0) }
    }

    if (pip?.isInPipMode == true) {
        Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
            VideoSurface(player = controller, modifier = Modifier.fillMaxSize())
        }
        return
    }

    PlayerScaffold(
        title = item.title,
        onBack = onBack,
        onEnterPip = if (isVideo) pip?.let { { it.enterPip() } } else null
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (isVideo) {
                VideoSurface(
                    player = controller,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                )
            } else {
                MediaThumbnail(
                    item = item,
                    modifier = Modifier.size(240.dp).clip(RoundedCornerShape(20.dp))
                )
            }
        }

        TransportBar(
            positionMs = position,
            durationMs = duration,
            isPlaying = isPlaying,
            onPlayPause = { if (isPlaying) controller.pause() else controller.play() },
            onSeek = { controller.seekTo(it) }
        )
    }
}

@Composable
private fun VideoSurface(player: Player, modifier: Modifier) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                setPlayer(player)
            }
        },
        update = { it.player = player },
        modifier = modifier
    )
}

@Composable
private fun TransportBar(
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit
) {
    var scrubMs by remember { mutableStateOf<Long?>(null) }
    val displayMs = scrubMs ?: positionMs
    val maxMs = durationMs.coerceAtLeast(1L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Slider(
            value = displayMs.coerceIn(0L, maxMs).toFloat(),
            onValueChange = { scrubMs = it.toLong() },
            onValueChangeFinished = {
                scrubMs?.let(onSeek)
                scrubMs = null
            },
            valueRange = 0f..maxMs.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = AppColors.Primary,
                activeTrackColor = AppColors.Primary,
                inactiveTrackColor = AppColors.Border
            )
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = MediaFormat.duration(displayMs),
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = MediaFormat.duration(durationMs),
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextMuted
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(AppColors.Primary)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = AppColors.TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerScaffold(
    title: String = "",
    onBack: () -> Unit,
    onEnterPip: (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
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
        Column(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
            Header(title = title, onBack = onBack, onEnterPip = onEnterPip)
            content()
        }
    }
}

@Composable
private fun Header(title: String, onBack: () -> Unit, onEnterPip: (() -> Unit)?) {
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
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = AppColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (onEnterPip != null) {
            IconButton(onClick = onEnterPip) {
                Icon(
                    imageVector = Icons.Filled.PictureInPicture,
                    contentDescription = "Picture in picture",
                    tint = AppColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
private fun CenteredMessage(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
