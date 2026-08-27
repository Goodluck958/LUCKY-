package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.TopicCategory
import com.example.ui.theme.*

@Composable
fun OmniMediaImage(
    mediaUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    when {
        mediaUrl.contains("ic_reel_cyber") -> {
            Image(
                painter = painterResource(id = R.drawable.ic_reel_cyber),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        mediaUrl.contains("ic_stream_thumb") -> {
            Image(
                painter = painterResource(id = R.drawable.ic_stream_thumb),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        mediaUrl.contains("ic_feed_photo") -> {
            Image(
                painter = painterResource(id = R.drawable.ic_feed_photo),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        mediaUrl.contains("ic_ad_banner") || mediaUrl.contains("ic_hero_ad_banner") -> {
            Image(
                painter = painterResource(id = R.drawable.ic_ad_banner),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        mediaUrl.contains("ic_omni_icon") -> {
            Image(
                painter = painterResource(id = R.drawable.ic_omni_icon),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        mediaUrl.startsWith("http") -> {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(mediaUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        else -> {
            // Fallback gradient aesthetic placeholder
            Box(
                modifier = modifier.background(
                    Brush.linearGradient(
                        colors = listOf(ObsidianSurfaceElevated, Color(0xFF1F2438))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = null,
                    tint = NeonCyan.copy(alpha = 0.6f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier, size: Int = 16) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(NeonCyan),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Verified Account",
            tint = Color.Black,
            modifier = Modifier.size((size * 0.7f).dp)
        )
    }
}

@Composable
fun AlgorithmBadge(
    affinityReason: String,
    algorithmScore: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color(0x99000000),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Algorithm Match",
                tint = NeonCyan,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = affinityReason,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CategoryPill(
    category: TopicCategory,
    modifier: Modifier = Modifier
) {
    val (color, name) = when (category) {
        TopicCategory.TECH -> NeonCyan to "Tech"
        TopicCategory.GAMING -> NeonPurple to "Gaming"
        TopicCategory.HUMOR -> BrightAmber to "Humor"
        TopicCategory.MUSIC -> SunsetPink to "Music"
        TopicCategory.LIFESTYLE -> Color(0xFFFF70A6) to "Lifestyle"
        TopicCategory.FITNESS -> NeonGreen to "Fitness"
        TopicCategory.CRYPTO -> Color(0xFFFFD166) to "Crypto"
        TopicCategory.SCIENCE -> ElectricBlue to "Science"
        TopicCategory.NEWS -> Color(0xFFE63946) to "News"
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Text(
            text = "#$name",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 18,
    tint: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.height(24.dp).width((barCount * 4).dp)) {
        val barWidth = size.width / (barCount * 2f)
        for (i in 0 until barCount) {
            val barHeight = if (isPlaying) {
                val wave = kotlin.math.sin(phase + (i * 0.5f)).toFloat()
                (size.height * 0.3f) + (size.height * 0.6f * ((wave + 1f) / 2f))
            } else {
                size.height * 0.2f
            }

            drawRoundRect(
                color = tint,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = i * barWidth * 2,
                    y = (size.height - barHeight) / 2
                ),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }
    }
}

@Composable
fun RotatingVinylSoundDisc(
    soundTitle: String,
    isPlaying: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Box(
        modifier = modifier
            .size(46.dp)
            .rotate(if (isPlaying) angle else 0f)
            .clip(CircleShape)
            .background(Color.Black)
            .border(2.dp, ObsidianBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(NeonCyan, NeonPurple)))
        )
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}
