package com.example.ui.pulse

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeedItemEntity
import com.example.ui.OmniViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PulseScreen(
    reels: List<FeedItemEntity>,
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    if (reels.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().background(ObsidianBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = TikTokRed,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Pulse Reels yet",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap + to post the first Short Video!",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { reels.size })

    VerticalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("pulse_vertical_pager")
    ) { page ->
        val reel = reels[page]
        PulseReelPage(
            reel = reel,
            isActive = pagerState.currentPage == page,
            onLike = { viewModel.toggleLike(reel.id, reel.isLiked) },
            onBookmark = { viewModel.toggleBookmark(reel.id, reel.isBookmarked) },
            onRepost = { viewModel.toggleRepost(reel.id, reel.isReposted) },
            onOpenComments = { viewModel.openCommentsForPost(reel.id) },
            onOpenAlgorithm = { viewModel.setAlgorithmTunerVisible(true) },
            onSendGift = { viewModel.openGiftSendDialog(reel) },
            onAdClicked = { if (reel.isSponsored) viewModel.onAdClicked(reel.id) }
        )
    }
}

@Composable
fun PulseReelPage(
    reel: FeedItemEntity,
    isActive: Boolean,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onRepost: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenAlgorithm: () -> Unit,
    onSendGift: () -> Unit,
    onAdClicked: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showHeartAnimation by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // Simulated continuous playback progress
    LaunchedEffect(isActive, isPlaying) {
        if (isActive && isPlaying) {
            while (true) {
                delay(100)
                playbackProgress = (playbackProgress + 0.015f) % 1f
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!reel.isLiked) onLike()
                        showHeartAnimation = true
                        scope.launch {
                            delay(800)
                            showHeartAnimation = false
                        }
                    },
                    onTap = {
                        isPlaying = !isPlaying
                    }
                )
            }
    ) {
        // Background Video Canvas
        OmniMediaImage(
            mediaUrl = reel.mediaUrl,
            contentDescription = reel.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Subtle gradient overlays for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Paused Indicator
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        // Animated double-tap heart
        if (showHeartAnimation) {
            val scale by animateFloatAsState(
                targetValue = 1.3f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "heartScale"
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = TikTokRed,
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .align(Alignment.Center)
            )
        }

        // Top Header: Algorithm Badge & Feed Format
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlgorithmBadge(
                affinityReason = reel.affinityReason,
                algorithmScore = reel.algorithmScore,
                onClick = onOpenAlgorithm
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryPill(category = reel.topicCategory)
                if (reel.isSponsored) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TikTokRed.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "SPONSORED",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Right-hand Side Action Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Avatar with Follow Badge
            Box(contentAlignment = Alignment.BottomCenter) {
                OmniMediaImage(
                    mediaUrl = reel.authorAvatarUrl,
                    contentDescription = reel.authorName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeonCyan, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .offset(y = 6.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(TikTokRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Like Action
            ActionColumnItem(
                icon = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                tint = if (reel.isLiked) TikTokRed else Color.White,
                label = formatCount(reel.likesCount),
                onClick = onLike,
                testTag = "pulse_like_button"
            )

            // Comments Action
            ActionColumnItem(
                icon = Icons.Default.ChatBubbleOutline,
                tint = Color.White,
                label = formatCount(reel.commentsCount),
                onClick = onOpenComments,
                testTag = "pulse_comments_button"
            )

            // Bookmark Action
            ActionColumnItem(
                icon = if (reel.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                tint = if (reel.isBookmarked) BrightAmber else Color.White,
                label = "Save",
                onClick = onBookmark,
                testTag = "pulse_bookmark_button"
            )

            // Virtual Gift Tip Action
            ActionColumnItem(
                icon = Icons.Default.CardGiftcard,
                tint = LuckyGold,
                label = "Tip ⏰",
                onClick = onSendGift,
                testTag = "pulse_gift_button"
            )

            // Repost Action
            ActionColumnItem(
                icon = Icons.Default.Repeat,
                tint = if (reel.isReposted) NeonGreen else Color.White,
                label = formatCount(reel.repostsCount),
                onClick = onRepost,
                testTag = "pulse_repost_button"
            )

            // Share Action
            ActionColumnItem(
                icon = Icons.Default.Share,
                tint = Color.White,
                label = formatCount(reel.sharesCount),
                onClick = { /* Share sheet simulated */ },
                testTag = "pulse_share_button"
            )

            // Rotating Vinyl Sound Disc
            RotatingVinylSoundDisc(
                soundTitle = reel.soundTitle,
                isPlaying = isPlaying
            )
        }

        // Bottom Left Content Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 28.dp)
        ) {
            // Author Name & Verified
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reel.authorName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (reel.authorVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    VerifiedBadge(size = 14)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reel.authorHandle,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Caption Text
            Text(
                text = reel.content,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // Sound Bar
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${reel.soundTitle} · ${reel.soundAuthor}",
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Sponsored CTA Button if Ad
            if (reel.isSponsored && reel.sponsorCtaText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAdClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TikTokRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("pulse_ad_cta_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reel.sponsorCtaText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Bottom Playback Progress Bar
        LinearProgressIndicator(
            progress = { playbackProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter),
            color = TikTokRed,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun ActionColumnItem(
    icon: ImageVector,
    tint: Color,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0)
        count >= 1_000 -> "%.1fK".format(count / 1_000.0)
        else -> count.toString()
    }
}
