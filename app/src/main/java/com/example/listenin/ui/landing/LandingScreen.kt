package com.example.listenin.ui.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listenin.ui.common.GenericScaffold
import com.example.listenin.ui.common.HeadphonesLogo
import com.example.listenin.ui.common.ScreenPadding
import com.example.listenin.ui.theme.AppColors
import org.intellij.lang.annotations.JdkConstants

@Composable
fun LandingScreen(
    onGetStartedClick: () -> Unit = {},
    onDemoClick: () -> Unit = {}
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
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = 140.dp, y = (-30).dp)
                    .background(
                        color = AppColors.Primary.copy(alpha = 0.28f),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = (-60).dp, y = 260.dp)
                    .background(
                        color = AppColors.Secondary.copy(alpha = 0.18f),
                        shape = CircleShape
                    )
            )

           TopBar()

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
            HeadphonesLogo(size = 34.dp, tint = AppColors.Primary)

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

@Composable
private fun ListeningPreviewCard() {
    Card(
        modifier = Modifier
            .width(360.dp)
            .height(420.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Now playing", color = AppColors.Secondary, fontWeight = FontWeight.Medium)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AppColors.Primary.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, AppColors.Primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶", color = AppColors.TextPrimary)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    AppColors.Primary,
                                    Color(0xFF7C3AED),
                                    AppColors.Secondary
                                )
                            )
                        )
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Midnight Audio Notes",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "A calm conversation about creative flow and building better habits.",
                        color = AppColors.TextSecondary
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "2:14 / 19:48",
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFF334155))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.35f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(999.dp))
                                .background(AppColors.Primary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureStat(label: String, sublabel: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = sublabel,
            style = MaterialTheme.typography.bodySmall.copy(color = AppColors.TextSecondary)
        )
    }
}

@Composable
private fun LandingChip(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = AppColors.Card.copy(alpha = 0.8f),
                shape = RoundedCornerShape(999.dp)
            )
            .border(1.dp, AppColors.Border, RoundedCornerShape(999.dp))
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(text = text, color = AppColors.TextSecondary)
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(200.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface.copy(alpha = 0.86f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(accent.copy(alpha = 0.18f), CircleShape)
                    .border(1.dp, accent.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("●", color = accent, fontWeight = FontWeight.Bold)
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

@Composable
private fun CTASection(onGetStartedClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = AppColors.Surface.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Build your listening ritual.",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create a calmer, smarter audio routine that follows your interests every day.",
                    color = AppColors.TextSecondary,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }

            Button(
                onClick = onGetStartedClick,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Join now")
            }
        }
    }
}
