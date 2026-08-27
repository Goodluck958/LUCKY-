package com.example.ui.lounge

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeedItemEntity
import com.example.data.model.StoryEntity
import com.example.ui.OmniViewModel
import com.example.ui.components.*
import com.example.ui.pulse.formatCount
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoungeScreen(
    feeds: List<FeedItemEntity>,
    stories: List<StoryEntity>,
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("lounge_lazy_column"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Stories Carousel Header (Instagram style)
        item {
            StoriesCarousel(
                stories = stories,
                onStoryClick = { viewModel.openStory(it) },
                onAddStory = { viewModel.setCreatePostVisible(true) }
            )
            Divider(color = ObsidianBorder.copy(alpha = 0.5f), thickness = 1.dp)
        }

        // 2. Feed Posts
        items(feeds) { post ->
            LoungePostCard(
                post = post,
                onLike = { viewModel.toggleLike(post.id, post.isLiked) },
                onBookmark = { viewModel.toggleBookmark(post.id, post.isBookmarked) },
                onComments = { viewModel.openCommentsForPost(post.id) },
                onAlgorithmClick = { viewModel.setAlgorithmTunerVisible(true) },
                onTip = { viewModel.openGiftSendDialog(post) },
                onAdClick = { if (post.isSponsored) viewModel.onAdClicked(post.id) }
            )
            Divider(color = ObsidianBorder.copy(alpha = 0.4f), thickness = 6.dp)
        }
    }
}

@Composable
fun StoriesCarousel(
    stories: List<StoryEntity>,
    onStoryClick: (StoryEntity) -> Unit,
    onAddStory: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "Your Story" item with plus
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onAddStory)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    OmniMediaImage(
                        mediaUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                        contentDescription = "Your Story",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                            .border(2.dp, ObsidianBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your Story",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        // Friends Stories with vibrant rainbow gradient rings
        items(stories) { story ->
            val ringBrush = if (story.hasUnseen) {
                Brush.sweepGradient(listOf(InstagramGradient1, InstagramGradient2, InstagramGradient3, InstagramGradient1))
            } else {
                Brush.linearGradient(listOf(ObsidianBorder, ObsidianBorder))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onStoryClick(story) }
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .border(2.5.dp, ringBrush, CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OmniMediaImage(
                        mediaUrl = story.authorAvatarUrl,
                        contentDescription = story.authorName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.authorName.split(" ").firstOrNull() ?: story.authorName,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LoungePostCard(
    post: FeedItemEntity,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onComments: () -> Unit,
    onAlgorithmClick: () -> Unit,
    onTip: () -> Unit,
    onAdClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(ObsidianSurface)) {
        // Author Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OmniMediaImage(
                mediaUrl = post.authorAvatarUrl,
                contentDescription = post.authorName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (post.authorVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(size = 13)
                    }
                }
                Text(
                    text = if (post.isSponsored) "Sponsored · ${post.sponsorBrand}" else post.authorHandle,
                    color = if (post.isSponsored) TikTokRed else TextMuted,
                    fontSize = 12.sp
                )
            }

            // Algorithm Badge
            AlgorithmBadge(
                affinityReason = post.affinityReason,
                algorithmScore = post.algorithmScore,
                onClick = onAlgorithmClick
            )
        }

        // Post Image with Double-Tap to Like
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { onLike() })
                }
        ) {
            OmniMediaImage(
                mediaUrl = post.mediaUrl.ifEmpty { post.thumbnailUrl },
                contentDescription = post.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Action Buttons Row (Like, Comment, Share, Bookmark)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) TikTokRed else TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = onComments, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onTip, modifier = Modifier.size(28.dp)) {
                    Text("⏰", fontSize = 16.sp)
                }
                IconButton(onClick = { /* Share */ }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            IconButton(onClick = onBookmark, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = if (post.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (post.isBookmarked) BrightAmber else TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Likes count & Caption text
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
            Text(
                text = "${formatCount(post.likesCount)} likes",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (post.title.isNotEmpty()) {
                Text(
                    text = post.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = post.content,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            // Sponsored CTA Bar if ad
            if (post.isSponsored && post.sponsorCtaText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SunsetPink.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SunsetPink),
                    onClick = onAdClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(post.sponsorCtaText, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(post.sponsorBrand, color = SunsetPink, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = SunsetPink)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // View all comments button
            Text(
                text = "View all ${formatCount(post.commentsCount)} comments",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier
                    .clickable(onClick = onComments)
                    .padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
