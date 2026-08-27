package com.example.ui.stream

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeedItemEntity
import com.example.ui.OmniViewModel
import com.example.ui.components.*
import com.example.ui.pulse.formatCount
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun StreamScreen(
    streams: List<FeedItemEntity>,
    selectedVideo: FeedItemEntity?,
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val currentVideo = selectedVideo ?: streams.firstOrNull()

    if (currentVideo == null) {
        Box(
            modifier = modifier.fillMaxSize().background(ObsidianBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No stream videos available. Tap + to upload!", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("stream_lazy_column"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. YouTube Video Player Viewport
        item {
            VideoPlayerViewport(
                video = currentVideo,
                onAlgorithmClick = { viewModel.setAlgorithmTunerVisible(true) },
                onAdClick = { viewModel.onAdClicked(currentVideo.id) }
            )
        }

        // 2. Video Title & Metadata
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = currentVideo.title.ifEmpty { "High Resolution Quantum Documentary" },
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${formatCount(currentVideo.viewsCount.toInt())} views · 2 hours ago",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    CategoryPill(category = currentVideo.topicCategory)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Creator Channel Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OmniMediaImage(
                        mediaUrl = currentVideo.authorAvatarUrl,
                        contentDescription = currentVideo.authorName,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentVideo.authorName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentVideo.authorVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VerifiedBadge(size = 12)
                            }
                        }
                        Text(
                            text = "2.4M subscribers",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Subscribe & Join Buttons
                    var isSubscribed by remember { mutableStateOf(false) }
                    Button(
                        onClick = { isSubscribed = !isSubscribed },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubscribed) ObsidianSurfaceElevated else YouTubeRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("stream_subscribe_button")
                    ) {
                        Text(
                            text = if (isSubscribed) "Subscribed ✓" else "Subscribe",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Horizontal Action Pills (Like, Dislike, Share, Remix, Thanks, Save)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Like / Dislike Combined Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ObsidianSurfaceElevated
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                modifier = Modifier
                                    .clickable { viewModel.toggleLike(currentVideo.id, currentVideo.isLiked) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (currentVideo.isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                                    contentDescription = "Like",
                                    tint = if (currentVideo.isLiked) NeonCyan else TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formatCount(currentVideo.likesCount),
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Divider(
                                modifier = Modifier
                                    .height(18.dp)
                                    .width(1.dp),
                                color = ObsidianBorder
                            )
                            Box(
                                modifier = Modifier
                                    .clickable { /* Dislike */ }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ThumbDown,
                                    contentDescription = "Dislike",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Share Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ObsidianSurfaceElevated,
                        onClick = { /* Share */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Super Thanks Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ObsidianSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.4f)),
                        onClick = { viewModel.openGiftSendDialog(currentVideo) },
                        modifier = Modifier.testTag("stream_tip_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⏰", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tip", color = LuckyGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description Box & Chapters
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = currentVideo.content,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Comments Teaser
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    onClick = { viewModel.openCommentsForPost(currentVideo.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Comments · ${formatCount(currentVideo.commentsCount)}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OmniMediaImage(
                                mediaUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
                                contentDescription = null,
                                modifier = Modifier.size(24.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Best technical breakdown of quantum principles!",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 3. "Up Next" Algorithmic Recommendations Header
        item {
            PaddingValues(top = 12.dp, bottom = 6.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Up Next · Algorithmic Queue",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Auto-play ON",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 4. Up Next Stream Items
        items(streams.filter { it.id != currentVideo.id }) { item ->
            StreamVideoCard(
                video = item,
                onClick = { viewModel.selectStreamVideo(item) }
            )
        }
    }
}

@Composable
fun VideoPlayerViewport(
    video: FeedItemEntity,
    onAlgorithmClick: () -> Unit,
    onAdClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableIntStateOf(142) }
    var selectedQuality by remember { mutableStateOf("1080p") }
    var showControls by remember { mutableStateOf(true) }
    var showQualityMenu by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(1000)
                currentSeconds += 1
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        OmniMediaImage(
            mediaUrl = video.thumbnailUrl.ifEmpty { video.mediaUrl },
            contentDescription = video.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                // Top Bar: Algorithm Pill & Quality
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AlgorithmBadge(
                        affinityReason = video.affinityReason,
                        algorithmScore = video.algorithmScore,
                        onClick = onAlgorithmClick
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.7f),
                            onClick = { showQualityMenu = !showQualityMenu }
                        ) {
                            Text(
                                text = "⚙️ $selectedQuality 60fps",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Center Play/Pause & Skip Buttons
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    IconButton(onClick = { currentSeconds = maxOf(0, currentSeconds - 10) }) {
                        Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    IconButton(onClick = { currentSeconds += 10 }) {
                        Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                // Bottom Timeline Scrubber
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatTime(currentSeconds)} / ${formatTime(video.videoDurationSeconds)}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Slider(
                        value = (currentSeconds.toFloat() / maxOf(1, video.videoDurationSeconds)).coerceIn(0f, 1f),
                        onValueChange = { currentSeconds = (it * video.videoDurationSeconds).toInt() },
                        colors = SliderDefaults.colors(
                            thumbColor = YouTubeRed,
                            activeTrackColor = YouTubeRed,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StreamVideoCard(
    video: FeedItemEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
        ) {
            OmniMediaImage(
                mediaUrl = video.thumbnailUrl.ifEmpty { video.mediaUrl },
                contentDescription = video.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Text(
                    text = formatTime(video.videoDurationSeconds),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OmniMediaImage(
                mediaUrl = video.authorAvatarUrl,
                contentDescription = video.authorName,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${video.authorName} · ${formatCount(video.viewsCount.toInt())} views",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
