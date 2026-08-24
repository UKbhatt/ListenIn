package com.example.listenin.ui.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.listenin.R
import com.example.listenin.ui.common.GenericScaffold
import com.example.listenin.ui.theme.AppColors

@Composable
fun LandingScreen(
    onPlayVideo: () -> Unit = {},
    onPlayAudio: () -> Unit = {}
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            AppColors.Background,
            AppColors.BackgroundSecondary,
            AppColors.BackgroundAccent
        )
    )

    GenericScaffold(
        isLoading = false,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.background(backgroundBrush)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            TopBar()

            MediaOptions(
                onPlayVideo = onPlayVideo,
                onPlayAudio = onPlayAudio,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun MediaOptions(
    onPlayVideo: () -> Unit,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onPlayAudio)
        ) {
            Image(
                painter = painterResource(id = R.drawable.audio_button_thumbnail),
                contentDescription = "Audio thumbnail",
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Play Audio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onPlayVideo)
        ) {
            Image(
                painter = painterResource(id = R.drawable.video_button_thumbnail),
                contentDescription = "Video thumbnail",
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Play Video",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "ListenIn logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "ListenIn",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}